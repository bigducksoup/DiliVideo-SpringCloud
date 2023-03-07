package com.ducksoup.dilivideofeign.BarrageServices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;


@Component
@FeignClient(value = "DiliVideo-Barrage")
public interface TestServices {

    @GetMapping("/hello")
    public String test();





}
