package com.ducksoup.dilivideoauth.controller.Feign;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideoauth.entity.Permission;
import com.ducksoup.dilivideoauth.entity.Role;
import com.ducksoup.dilivideoauth.entity.RolePermission;
import com.ducksoup.dilivideoauth.entity.UserRole;
import com.ducksoup.dilivideoauth.mainServices.AuthService;
import com.ducksoup.dilivideoauth.service.PermissionService;
import com.ducksoup.dilivideoauth.service.RolePermissionService;
import com.ducksoup.dilivideoauth.service.RoleService;
import com.ducksoup.dilivideoauth.service.UserRoleService;
import com.ducksoup.dilivideoentity.admin.params.PermissionGrantParam;
import com.ducksoup.dilivideoentity.admin.params.RevokeRolePermissionParam;
import com.ducksoup.dilivideoentity.admin.params.RevokeUserRoleParam;
import com.ducksoup.dilivideoentity.admin.params.RoleAssignParam;
import com.ducksoup.dilivideoentity.authEntity.params.PermissionAddParam;
import com.ducksoup.dilivideoentity.authEntity.params.RoleAddParam;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feign/auth")
public class PermissionController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @GetMapping("/get_roles")
    public List<String> getRoles(@RequestParam String loginId){
       return authService.getRoleList(loginId);
    }

    @GetMapping("/get_permissions")
    public List<String> getPermissions(@RequestParam String loginId){
        return authService.getPermissionList(loginId);
    }

    /**
     * 添加角色信息
     * @param param RoleAddParam
     * @return boolean
     */
    @PostMapping("/add_role")
    public boolean addRole(@RequestBody RoleAddParam param){

        Role role = new Role();
        role.setId(UUID.randomUUID().toString());
        role.setRoleName(param.getRoleName());
        DateTime now = DateTime.now();
        role.setCreateTime(now);
        role.setUpdateTime(now);
        role.setStatus(1);
        role.setNote(param.getNote());

        return roleService.save(role);
    }

    /**
     * 添加权限信息
     * @param param PermissionAddParam
     * @return boolean
     */
    @PostMapping("/add_permission")
    public boolean addPermission(@RequestBody PermissionAddParam param){

        Permission permission = new Permission();
        permission.setId(UUID.randomUUID().toString());
        permission.setPermissionName(param.getPermissionName());
        DateTime now = DateTime.now();
        permission.setCreateTime(now);
        permission.setUpdateTime(now);
        permission.setNote(param.getNote());
        permission.setStatus(1);


        return permissionService.save(permission);
    }


    /**
     * 向用户赋予角色
     * @param param RoleAssignParam
     * @return boolean
     */
    @PostMapping("/assign_role")
    public boolean assignRoleToUser(@RequestBody RoleAssignParam param){
        UserRole userRole = new UserRole();
        userRole.setId(UUID.randomUUID().toString());
        userRole.setUserId(param.getUserId());
        userRole.setRoleId(param.getRoleId());
        userRole.setNote(param.getNote());
        userRole.setStatus(1);

        return userRoleService.save(userRole);
    }


    /**
     * 向角色添加权限
     * @param param PermissionGrantParam
     * @return boolean
     */
    @PostMapping("/grant_permission")
    public boolean GrantPermissionToRole(@RequestBody PermissionGrantParam param){

        RolePermission rolePermission = new RolePermission();
        rolePermission.setId(UUID.randomUUID().toString());
        rolePermission.setRoleId(param.getRoleId());
        rolePermission.setPermissionId(param.getPermissionId());
        rolePermission.setNote(param.getNote());
        rolePermission.setStatus(1);

        return rolePermissionService.save(rolePermission);

    }


    /**
     * 解除用户身份 0为彻底删除 1为逻辑删除
     * @param param RevokeUserRoleParam
     * @return boolean
     */
    @PostMapping("/revoke_user_role")
    public boolean revokeRoleFromUser(@RequestBody RevokeUserRoleParam param){

        if (param.getMode() == 0){
            return userRoleService.remove(new LambdaUpdateWrapper<UserRole>()
                    .eq(UserRole::getUserId,param.getUserId())
                    .eq(UserRole::getRoleId,param.getRoleId()));
        }

        return userRoleService.update(new LambdaUpdateWrapper<UserRole>()
                .eq(UserRole::getUserId,param.getUserId())
                .eq(UserRole::getRoleId,param.getRoleId())
                .set(UserRole::getStatus,0));

    }


    @PostMapping("/revoke_role_permission")
    public boolean revokePermissionFromRole(@RequestBody RevokeRolePermissionParam param){

        if (param.getMode() == 0){
            rolePermissionService.remove(new LambdaQueryWrapper<RolePermission>()
                    .eq(RolePermission::getRoleId,param.getRoleId())
                    .eq(RolePermission::getPermissionId,param.getPermissionId()));
        }

        return rolePermissionService.update(new LambdaUpdateWrapper<RolePermission>()
                .eq(RolePermission::getRoleId,param.getRoleId())
                .eq(RolePermission::getPermissionId,param.getPermissionId())
                .set(RolePermission::getStatus,0));

    }


}
