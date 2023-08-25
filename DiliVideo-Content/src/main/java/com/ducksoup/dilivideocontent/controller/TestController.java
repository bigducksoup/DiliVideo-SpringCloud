package com.ducksoup.dilivideocontent.controller;


import com.ducksoup.dilivideofeign.barrage.TestServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin
@RestController
public class TestController {




    @Autowired
    TestServices testServices;

    @RequestMapping("/")
    public String fuck(){
        return "212121";
    }

    @RequestMapping("/test")
    public String Test(){

        System.out.println(123);
        return testServices.test();
    }
}
