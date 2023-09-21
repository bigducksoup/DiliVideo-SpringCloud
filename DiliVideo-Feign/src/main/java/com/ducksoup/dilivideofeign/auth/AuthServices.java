package com.ducksoup.dilivideofeign.auth;


import com.ducksoup.dilivideoentity.admin.params.PermissionGrantParam;
import com.ducksoup.dilivideoentity.admin.params.RevokeRolePermissionParam;
import com.ducksoup.dilivideoentity.admin.params.RevokeUserRoleParam;
import com.ducksoup.dilivideoentity.admin.params.RoleAssignParam;
import com.ducksoup.dilivideoentity.auth.Avatar;
import com.ducksoup.dilivideoentity.auth.MUser;
import com.ducksoup.dilivideoentity.auth.params.PermissionAddParam;
import com.ducksoup.dilivideoentity.auth.params.RoleAddParam;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideofeign.Inteceptor.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient(value = "DiliVideo-Auth",configuration = FeignInterceptor.class)
public interface AuthServices {

    @GetMapping(path = "feign/info/getById")
    ResponseResult<MUser> getUserInfo(@RequestParam(value = "userId") String userId);


    @GetMapping(path = "feign/info/getAvatarInfo")
    ResponseResult<Avatar> getAvatarInfo(@RequestParam(value = "avatarId") String avatarId);


    @GetMapping(path = "feign/auth/get_roles")
    List<String> getRolesByLoginId(@RequestParam(value = "loginId")String loginId);

    @GetMapping(path = "feign/auth/get_permissions")
    List<String> getPermissionsByLoginId(@RequestParam(value = "loginId")String loginId);

    @PostMapping(path = "feign/auth/add_role")
    boolean addRole(@RequestBody RoleAddParam param);

    @PostMapping(path = "feign/auth/add_permission")
    boolean addPermission(@RequestBody PermissionAddParam param);

    @PostMapping(path = "feign/auth/assign_role")
    boolean assignRoleToUser(@RequestBody RoleAssignParam param);

    @PostMapping(path = "feign/auth/grant_permission")
    boolean GrantPermissionToRole(@RequestBody PermissionGrantParam param);

    @PostMapping(path = "feign/auth/revoke_user_role")
    boolean revokeRoleFromUser(@RequestBody RevokeUserRoleParam param);

    @PostMapping(path = "feign/auth/revoke_role_permission")
    boolean revokePermissionFromRole(@RequestBody RevokeRolePermissionParam param);

}
