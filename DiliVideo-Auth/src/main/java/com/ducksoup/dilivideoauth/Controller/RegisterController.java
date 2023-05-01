package com.ducksoup.dilivideoauth.Controller;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideoauth.Controller.Params.RegisterParam;
import com.ducksoup.dilivideoauth.Entity.MUser;
import com.ducksoup.dilivideoauth.mainServices.MailSenderService;
import com.ducksoup.dilivideoauth.service.MUserService;
import com.ducksoup.dilivideoauth.utils.RedisUtil;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideoentity.dto.FileUploadDTO;
import com.ducksoup.dilivideofeign.Content.ContentServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/register")
@Slf4j
public class RegisterController {


    @Autowired
    private ContentServices contentServices;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MailSenderService QQmailSenderServiceImpl;

    @Autowired
    private MUserService userService;


    /**
     * 提交注册表单
     * @param param 验证码 和 其他信息 （头像验证码可选）
     * @return ResponseResult<Boolean>
     */
    @PostMapping("/submit_form")
    public ResponseResult<Boolean> registerByEmail(@RequestBody RegisterParam param) {

        String email = (String) redisUtil.get(param.getCode());
        if (email == null) {
            return new ResponseResult<Boolean>(HttpStatus.HTTP_NO_CONTENT, "验证码失效", false);
        }
        redisUtil.remove(param.getCode());
        //获取头像
        String avatarUrl = param.getAvatarCode().equals("") ? "" : (String) redisUtil.get(param.getAvatarCode());

        redisUtil.remove(param.getAvatarCode());

        //加密密码
        String encodePassword = DigestUtil.md5Hex(param.getPassword());

        DateTime now = DateTime.now();

        MUser user = new MUser();
        user.setId(UUID.randomUUID().toString());
        user.setNickname(param.getNickname());
        user.setEmail(email);
        user.setPassword(encodePassword);
        user.setAvatarUrl(avatarUrl);
        user.setSummary("还没有简介");
        user.setFollowerCount(0L);
        user.setFollowedCount(0L);
        user.setPublishCount(0);
        user.setIsBaned(0);
        user.setLevel(0);
        user.setExp(0);
        user.setPhoneNumber("");
        user.setBirthday(now);
        user.setGender(param.getGender());
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setLoginIp("");
        user.setWechatId("");

        boolean save = userService.save(user);

        if (save) {
            log.info(user.getEmail() + "...注册成功");
            return new ResponseResult<>(HttpStatus.HTTP_OK, "注册成功", true);
        } else {
            log.error(user.getEmail() + "...注册失败");
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "注册失败", false);
        }
    }


    /**
     * 通过邮箱获取验证码
     *
     * @param email 邮箱
     * @return ResponseResult<Boolean>
     */
    @GetMapping("/get_code_by_email")
    public ResponseResult<Boolean> getCodeByEmail(@RequestParam String email) {

        if (redisUtil.exists("reg:" + email)) {
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN, "请稍后再试", false);
        }

        MUser user = userService.getOne(new LambdaQueryWrapper<MUser>().eq(MUser::getEmail, email));
        if (user!=null){
            return new ResponseResult<>(HttpStatus.HTTP_CONFLICT,"邮箱已经注册",false);
        }

        String randomNumber = RandomUtil.randomNumbers(6);
        while (redisUtil.exists(randomNumber)) {
            randomNumber = RandomUtil.randomNumbers(6);
        }

        //设置验证码
        redisUtil.set(randomNumber, email, 10L, TimeUnit.MINUTES);
        //限制发送频率
        redisUtil.set("reg:" + email, null, 1L, TimeUnit.MINUTES);

        try {
            QQmailSenderServiceImpl.sendVerifyCodeMail(email, randomNumber);
            return new ResponseResult<>(HttpStatus.HTTP_OK, "验证码发送成功", true);
        } catch (Exception e) {
            return new ResponseResult<>(HttpStatus.HTTP_OK, "验证码发送失败", false);
        }


    }


    /**
     * 上传用户头像
     *
     * @param file 头像文件
     * @return ResponseResult<String> 文件地址
     */
    @PostMapping("/upload_avatar")
    public ResponseResult<String> uploadAvatar(MultipartFile file) {


        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFile(file);
        fileUploadDTO.setBucketName("avatar");

        //远程调用上传头像
        ResponseResult<String> result = contentServices.uploadFile(fileUploadDTO);

        if (result.getCode() != 200) {
            log.error("远程调用失败：auth->content,msg:" + result.getMsg());
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "上传失败");
        }

        String randomNumber = RandomUtil.randomNumbers(10);

        while (redisUtil.exists(randomNumber)) {
            randomNumber = RandomUtil.randomNumbers(10);
        }

        redisUtil.set(randomNumber,result.getData(),10L,TimeUnit.MINUTES);

        return new ResponseResult<>(HttpStatus.HTTP_OK, "上传成功", randomNumber);
    }


}
