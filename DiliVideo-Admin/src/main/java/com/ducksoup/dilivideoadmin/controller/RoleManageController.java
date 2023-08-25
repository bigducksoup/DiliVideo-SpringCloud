package com.ducksoup.dilivideoadmin.controller;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.admin.params.PermissionGrantParam;
import com.ducksoup.dilivideoentity.admin.params.RevokeRolePermissionParam;
import com.ducksoup.dilivideoentity.admin.params.RevokeUserRoleParam;
import com.ducksoup.dilivideoentity.admin.params.RoleAssignParam;
import com.ducksoup.dilivideoentity.authEntity.params.PermissionAddParam;
import com.ducksoup.dilivideoentity.authEntity.params.RoleAddParam;
import com.ducksoup.dilivideoentity.constant.CONSTANT_ROLE;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@Validated
@Valid
@SaCheckRole(value = CONSTANT_ROLE.SUPER_ADMIN)
@RestController
@RequestMapping("/role_manage")
public class RoleManageController {

    @Autowired
    private AuthServices authServices;


    /**
     * 添加系统角色
     * @param param RoleAddParam
     * @return boolean
     * @see RoleAddParam
     */
    @PostMapping("/add_role")
    public ResponseResult<Boolean> addRole(@RequestBody RoleAddParam param){

        if (authServices.addRole(param)){
            return new ResponseResult<>(HttpStatus.HTTP_OK,"添加成功",true);
        }

        return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"添加失败",false);
    }


    /**
     * 添加系统权限
     * @param param RoleAddParam
     * @return boolean
     * @see com.ducksoup.dilivideoentity.authEntity.params.PermissionAddParam
     */
    @PostMapping("/add_permission")
    public ResponseResult<Boolean> addPermission(@RequestBody PermissionAddParam param){

        if (authServices.addPermission(param)){
            return new ResponseResult<>(HttpStatus.HTTP_OK,"添加成功",true);
        }

        return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"添加失败",false);
    }

    /**
     * assign role to user
     * @param param RoleAssignParam
     * @return Boolean
     */
    @PostMapping("/assign_role")
    public ResponseResult<Boolean> assignRoleToUser(@RequestBody RoleAssignParam param){
        return new ResponseResult<>(HttpStatus.HTTP_OK,"用户角色成功",authServices.assignRoleToUser(param));
    }

    /**
     * grant permission to role
     * @param param PermissionGrantParam
     * @return Boolean
     */
    @PostMapping("/grant_permission")
    public ResponseResult<Boolean> GrantPermissionToRole(@RequestBody PermissionGrantParam param){
        return new ResponseResult<>(HttpStatus.HTTP_OK,"角色授权成功",authServices.GrantPermissionToRole(param));
    }

    /**
     * revoke role from user
     * @param param RevokeUserRoleParam
     * @return Boolean
     */
    @PostMapping("/revoke_user_role")
    public ResponseResult<Boolean> revokeRoleFromUser(@Validated @RequestBody RevokeUserRoleParam param){
        return new ResponseResult<>(HttpStatus.HTTP_OK,"撤销用户角色",authServices.revokeRoleFromUser(param));
    }

    /**
     * revoke permission from role
     * @param param RevokeRolePermissionParam
     * @return Boolean
     */
    @PostMapping("/revoke_role_permission")
    public ResponseResult<Boolean> revokePermissionFromRole(@RequestBody RevokeRolePermissionParam param){
        return new ResponseResult<>(HttpStatus.HTTP_OK,"撤销角色权限",authServices.revokePermissionFromRole(param));
    }
}
