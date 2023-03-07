package com.ducksoup.dilivideoauth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideoauth.Controller.Params.LoginByEmailParam;
import com.ducksoup.dilivideoauth.Entity.MUser;
import com.ducksoup.dilivideoauth.service.LoginService;
import com.ducksoup.dilivideoauth.service.MUserService;
import com.ducksoup.dilivideoentity.vo.LoginUserInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private MUserService userService;

    @Override
    public LoginUserInfoVo loginbyemail(LoginByEmailParam param) {


        //通过email获取用户
        MUser user = userService.getOne(new LambdaQueryWrapper<MUser>().eq(MUser::getEmail, param.getEmail()));

        if (user==null){
            return null;
        }

        if (!user.getPassword().equals(param.getPassword())){
            return null;
        }

        //登录
        StpUtil.login(user.getId());
        //获取tokeninfo
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        LoginUserInfoVo loginUserInfoVo = new LoginUserInfoVo();
        //设置信息
        BeanUtils.copyProperties(user,loginUserInfoVo);

        loginUserInfoVo.setAuthToken(tokenInfo.tokenValue);

        return loginUserInfoVo;

    }
}
