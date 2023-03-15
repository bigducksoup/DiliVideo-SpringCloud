package com.ducksoup.dilivideoauth.Controller.Feign;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoauth.Entity.MUser;
import com.ducksoup.dilivideoauth.service.MUserService;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
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


    @GetMapping("/getById")
    public ResponseResult<MUser> getUserInfo(@RequestParam String userId){
        MUser user = userService.getById(userId);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取信息成功",user);
    }

}
