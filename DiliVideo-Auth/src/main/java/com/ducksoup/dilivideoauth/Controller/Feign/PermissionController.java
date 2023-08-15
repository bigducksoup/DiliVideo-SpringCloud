package com.ducksoup.dilivideoauth.Controller.Feign;

import cn.dev33.satoken.stp.StpInterface;
import com.ducksoup.dilivideoauth.mainServices.AuthService;
import com.ducksoup.dilivideoauth.mainServices.Impl.StpInterfaceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feign/auth")
public class PermissionController {

    @Autowired
    private AuthService authService;

    @GetMapping("/get_roles")
    public List<String> getRoles(@RequestParam String loginId){
       return authService.getRoleList(loginId);
    }

    @GetMapping("/get_permissions")
    public List<String> getPermissions(@RequestParam String loginId){
        return authService.getPermissionList(loginId);
    }


}
