package com.ducksoup.dilivideolive.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/room")
public class LiveRoomController {


    @Autowired
    private AuthServices authServices;

    @SaCheckLogin
    @PostMapping("/get_permission")
    public ResponseResult<Boolean> getLivePermission(){
        String loginId = (String) StpUtil.getLoginId();

        List<String> roles = authServices.getRolesByLoginId(loginId);

        if (roles.contains("ANCHOR")){
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN,"请勿重复申请",false);
        }

        //TODO 申请直播权限
        return null;


    }


}
