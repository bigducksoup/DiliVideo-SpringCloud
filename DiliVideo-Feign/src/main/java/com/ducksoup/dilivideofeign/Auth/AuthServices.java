package com.ducksoup.dilivideofeign.Auth;


import com.ducksoup.dilivideoentity.AuthEntity.Avatar;
import com.ducksoup.dilivideoentity.AuthEntity.MUser;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideofeign.Inteceptor.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(value = "DiliVideo-Auth",configuration = FeignInterceptor.class)
public interface AuthServices {

    @GetMapping(path = "feign/info/getById")
    ResponseResult<MUser> getUserInfo(@RequestParam(value = "userId") String userId);


    @GetMapping(path = "feign/info/getAvatarInfo")
    ResponseResult<Avatar> getAvatarInfo(@RequestParam(value = "avatarId") String avatarId);


}
