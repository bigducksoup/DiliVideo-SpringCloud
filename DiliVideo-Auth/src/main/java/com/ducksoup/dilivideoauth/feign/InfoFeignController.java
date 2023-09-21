package com.ducksoup.dilivideoauth.feign;

import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoauth.entity.Avatar;
import com.ducksoup.dilivideoauth.entity.MUser;
import com.ducksoup.dilivideoauth.service.AvatarService;
import com.ducksoup.dilivideoauth.service.MUserService;
import com.ducksoup.dilivideoauth.utils.OSSUtils;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign/info")
public class InfoFeignController {

    @Autowired
    private MUserService userService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private OSSUtils ossUtils;


    @GetMapping("/getById")
    public ResponseResult<MUser> getUserInfo(@RequestParam String userId){
        MUser user = userService.getById(userId);

        Avatar avatar = avatarService.getById(user.getAvatarId());
        user.setAvatarUrl(ossUtils.makeUrl(avatar.getBucket(), avatar.getPath()));

        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取信息成功",user);
    }


    @GetMapping("/getAvatarInfo")
    public ResponseResult<Avatar> getAvatarInfoById(@RequestParam String avatarId){

        Avatar avatar = avatarService.getById(avatarId);
        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取头像信息成功",avatar);

    }

}
