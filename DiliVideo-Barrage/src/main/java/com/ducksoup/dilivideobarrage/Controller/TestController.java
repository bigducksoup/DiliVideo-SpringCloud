package com.ducksoup.dilivideobarrage.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {


    @RequestMapping("/hello")
    public String test(){
        return "hello";
    }

}
