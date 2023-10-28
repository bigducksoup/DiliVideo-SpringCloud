package com.ducksoup.dilivideofeign.main;


import com.ducksoup.dilivideoentity.main.TextUserInfoUpdateParams;
import com.ducksoup.dilivideofeign.Inteceptor.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "DiliVideo-Main",configuration = FeignInterceptor.class)
public interface TextInfoServices {


    @PostMapping("/feign/text_info/update_user_info")
    Boolean updateUserInfo(@RequestBody TextUserInfoUpdateParams params);


}
