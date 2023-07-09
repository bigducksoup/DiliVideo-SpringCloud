package com.ducksoup.dilivideoauth.Controller;


import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoauth.Entity.Avatar;
import com.ducksoup.dilivideoauth.Entity.MUser;
import com.ducksoup.dilivideoauth.service.AvatarService;
import com.ducksoup.dilivideoauth.service.MUserService;
import com.ducksoup.dilivideoauth.utils.OSSUtils;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
