package com.ducksoup.dilivideocontent.Controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideocontent.dto.FileTransmissionInfo;
import com.ducksoup.dilivideocontent.mainservices.MinIOImpl.UploadService;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideoentity.Result.BaseResponse;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UploadService uploadService;

    @SaCheckLogin
    @PostMapping("/upload")
    public BaseResponse<Boolean> uploadVideo( FileTransmissionInfo fileTransmissionInfo){


        try {
            String s = uploadService.uploadVideo(fileTransmissionInfo.getFile());
        } catch (Exception e){
            System.out.println(e);
        }


        return null;
    }

    @SaCheckLogin
    @GetMapping("/getuploadcode")
    public ResponseResult<String> getUploadCode(){

        //获取登录id
        String loginId = (String) StpUtil.getLoginId();

        if (redisUtil.exists("videoUploadCode:"+loginId)){
            redisUtil.remove("videoUploadCode:"+loginId);
        }
        //随机一个code
        String s = RandomUtil.randomNumbers(6);

        redisUtil.set("videoUploadCode:"+loginId,s);

        return new ResponseResult<String>(HttpStatus.HTTP_OK,"获取code成功",s);
    }



}
