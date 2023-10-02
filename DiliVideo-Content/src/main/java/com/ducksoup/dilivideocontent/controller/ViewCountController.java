package com.ducksoup.dilivideocontent.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideocontent.aop.annonation.PerformanceLog;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.ViewsService;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/views")
public class ViewCountController {


    @Autowired
    private ViewsService viewsService;

    @SaCheckLogin
    @GetMapping("/add_view")
    public ResponseResult<Long> addView(@RequestParam String videoInfoId){

        Long views = viewsService.addViews(videoInfoId);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"操作成功",views);

    }

}
