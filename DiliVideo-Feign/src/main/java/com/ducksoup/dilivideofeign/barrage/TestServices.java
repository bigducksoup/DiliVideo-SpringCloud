package com.ducksoup.dilivideofeign.barrage;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;


@Component
@FeignClient(value = "DiliVideo-Barrage")
public interface TestServices {

    @GetMapping("/hello")
    public String test();





}
