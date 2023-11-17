package com.ducksoup.dilivideolive.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import com.ducksoup.dilivideolive.mainservices.RoleApplicationService;
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

    @Autowired
    private RoleApplicationService roleApplicationService;


    /**
     * 用户申请直播权限
     * @return ResponseResult<Boolean>
     */
    @SaCheckLogin
    @PostMapping("/get_permission")
    public ResponseResult<Boolean> getLivePermission(){
        String loginId = (String) StpUtil.getLoginId();

        List<String> roles = authServices.getRolesByLoginId(loginId);

        if (roles.contains("ANCHOR")){
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN,"请勿重复申请",false);
        }

        // 申请直播权限
        boolean replyResult = roleApplicationService.replyForAnchorRole(loginId);

        if (!replyResult){
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"申请失败请重试", false);
        }

        return new ResponseResult<>(HttpStatus.HTTP_OK,"申请已经发出",true);


    }


}
