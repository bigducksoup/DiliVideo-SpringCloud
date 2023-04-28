package com.ducksoup.dilivideoauth.Controller;


import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoauth.Entity.MUser;
import com.ducksoup.dilivideoauth.service.MUserService;
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

    @GetMapping("/basic")
    public ResponseResult<UserVo> getUserBasicInfo(@RequestParam String userId){

        MUser user = userService.getById(userId);

        if (user==null){
            return new ResponseResult<>(HttpStatus.HTTP_NO_CONTENT,"查无此人");
        }

        UserVo userVo = new UserVo();

        BeanUtils.copyProperties(user,userVo);
        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取用户信息成功",userVo);

    }

}
