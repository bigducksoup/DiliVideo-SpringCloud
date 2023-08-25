package com.ducksoup.dilivideoauth.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoauth.controller.Params.LoginByEmailParam;
import com.ducksoup.dilivideoauth.entity.Avatar;
import com.ducksoup.dilivideoauth.entity.MUser;
import com.ducksoup.dilivideoauth.service.AvatarService;
import com.ducksoup.dilivideoauth.service.LoginService;
import com.ducksoup.dilivideoauth.service.MUserService;
import com.ducksoup.dilivideoauth.utils.OSSUtils;
import com.ducksoup.dilivideoentity.result.ResponseResult;

import com.ducksoup.dilivideoentity.vo.LoginUserInfoVo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/login")
public class LoginController {


    @Autowired
    private LoginService loginService;

    @Autowired
    private MUserService userService;

    @Autowired
    private OSSUtils ossUtils;

    @Autowired
    private AvatarService avatarService;

    @PostMapping("/login_by_email")
    public ResponseResult<LoginUserInfoVo> login(@RequestBody LoginByEmailParam param){
        LoginUserInfoVo loginbyemail = loginService.loginbyemail(param);

        if (loginbyemail==null){
            return new ResponseResult<>(HttpStatus.HTTP_UNAUTHORIZED,"登录失败,用户名或密码错误！");
        }

        return new ResponseResult<LoginUserInfoVo>(HttpStatus.HTTP_OK,"登录成功",loginbyemail);
    }

    @SaCheckLogin
    @GetMapping("/check_login")
    public ResponseResult<LoginUserInfoVo> checklogin(@RequestHeader String token){
        String loginId = (String) StpUtil.getLoginId();


        //获取tokeninfo
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        MUser user = userService.getById(loginId);

        Avatar avatar = avatarService.getById(user.getAvatarId());

        String url = ossUtils.makeUrl(avatar.getBucket(), avatar.getPath());

        user.setAvatarUrl(url);

        LoginUserInfoVo loginUserInfoVo = new LoginUserInfoVo();
        //设置信息
        BeanUtils.copyProperties(user,loginUserInfoVo);

        loginUserInfoVo.setAuthToken(tokenInfo.tokenValue);


        return new ResponseResult<>(HttpStatus.HTTP_OK,"token有效",loginUserInfoVo);
    }


}
