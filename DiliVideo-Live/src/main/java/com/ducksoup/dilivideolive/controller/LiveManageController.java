package com.ducksoup.dilivideolive.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.ducksoup.dilivideoentity.constant.CONSTANT_ROLE;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/control")
@SaCheckRole(CONSTANT_ROLE.SUPER_ADMIN)
public class LiveManageController {









}
