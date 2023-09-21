package com.ducksoup.dilivideofeign.admin;

import com.ducksoup.dilivideoentity.admin.params.RoleApplicationParam;
import com.ducksoup.dilivideofeign.Inteceptor.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "DiliVideo-Admin",configuration = FeignInterceptor.class)
public interface RoleServices {

    @PostMapping("feign/role/application")
    boolean RoleApplicationAction(@RequestBody RoleApplicationParam param);

}
