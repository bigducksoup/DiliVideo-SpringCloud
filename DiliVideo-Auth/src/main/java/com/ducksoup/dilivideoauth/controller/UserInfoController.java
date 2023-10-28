package com.ducksoup.dilivideoauth.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideoauth.controller.Params.UserInfoUpdateParams;
import com.ducksoup.dilivideoauth.entity.Avatar;
import com.ducksoup.dilivideoauth.entity.MUser;
import com.ducksoup.dilivideoauth.service.AvatarService;
import com.ducksoup.dilivideoauth.service.MUserService;
import com.ducksoup.dilivideoauth.utils.OSSUtils;
import com.ducksoup.dilivideoentity.content.VideoAuthorInfoUpdateParams;
import com.ducksoup.dilivideoentity.main.TextUserInfoUpdateParams;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.UserVo;
import com.ducksoup.dilivideofeign.content.VideoInfoServices;
import com.ducksoup.dilivideofeign.main.TextInfoServices;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RequestMapping("/user_info")
@RestController
public class UserInfoController {

    @Autowired
    private MUserService userService;

    @Autowired
    private OSSUtils ossUtils;

    @Autowired
    private AvatarService avatarService;

    @Resource
    private TextInfoServices textInfoServices;

    @Resource
    private VideoInfoServices videoInfoServices;

    @GetMapping("/basic")
    public ResponseResult<UserVo> getUserBasicInfo(@RequestParam String userId){

        MUser user = userService.getById(userId);

        if (user==null){
            return new ResponseResult<>(HttpStatus.HTTP_NO_CONTENT,"查无此人");
        }

        UserVo userVo = new UserVo();

        Avatar avatar = avatarService.getById(user.getAvatarId());

        String url = ossUtils.makeUrl(avatar.getBucket(), avatar.getPath());

        user.setAvatarUrl(url);

        BeanUtils.copyProperties(user,userVo);
        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取用户信息成功",userVo);

    }



    /**
     * 更新个人用户信息
     * @param params UserInfoUpdateParams
     * @return Boolean
     * TODO test
     */
    @SaCheckLogin
    @PostMapping("/update")
    public ResponseResult<Boolean> updateUserInfo(@RequestBody @Validated UserInfoUpdateParams params){

        String loginId = (String) StpUtil.getLoginId();
        DateTime time = null;
        if (params.getBirthDay()!=null){
            time = DateUtil.parse(params.getBirthDay(), "yyyy-MM-dd");
        }

        //更新基本信息
        boolean update = userService.update(new LambdaUpdateWrapper<MUser>()
                .eq(MUser::getId, loginId)
                .set(params.getName() != null, MUser::getNickname, params.getName())
                .set(params.getSummary() != null, MUser::getSummary, params.getSummary())
                .set(time != null, MUser::getBirthday, time)
                .set(params.getGender() != null, MUser::getGender, params.getGender())
        );

        //更新视频信息中的作者信息
        VideoAuthorInfoUpdateParams videoAuthorInfoUpdateParams = new VideoAuthorInfoUpdateParams();
        videoAuthorInfoUpdateParams.setNickName(params.getName());
        videoAuthorInfoUpdateParams.setSummary(params.getSummary());
        videoAuthorInfoUpdateParams.setAuthorId(loginId);
        videoInfoServices.updateVideoAuthorInfo(videoAuthorInfoUpdateParams);


        // 更新Post和Comment中的作者信息
        TextUserInfoUpdateParams textUserInfoUpdateParams = new TextUserInfoUpdateParams();
        textUserInfoUpdateParams.setNickName(params.getName());
        textUserInfoUpdateParams.setUserId(loginId);

        textInfoServices.updateUserInfo(textUserInfoUpdateParams);

        return update? new ResponseResult<>(HttpStatus.HTTP_OK,"更新成功",true):
                new ResponseResult<>(HttpStatus.HTTP_OK,"更新失败",false);


    }


}
