package com.ducksoup.dilivideocontent.Controller;

import com.ducksoup.dilivideofeign.BarrageServices.TestServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    @Autowired
    TestServices testServices;



    @RequestMapping("/test")
    public String Test(){
        System.out.println(123);
        return testServices.test();
    }
}
