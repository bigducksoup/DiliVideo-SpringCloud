package com.ducksoup.dilivideoauth.mainServices.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideoauth.entity.Permission;
import com.ducksoup.dilivideoauth.entity.Role;
import com.ducksoup.dilivideoauth.entity.RolePermission;
import com.ducksoup.dilivideoauth.entity.UserRole;
import com.ducksoup.dilivideoauth.mainServices.AuthService;
import com.ducksoup.dilivideoauth.service.PermissionService;
import com.ducksoup.dilivideoauth.service.RolePermissionService;
import com.ducksoup.dilivideoauth.service.RoleService;
import com.ducksoup.dilivideoauth.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private PermissionService permissionService;


    public List<String> getPermissionList(Object loginId) {

        List<String> roleIds = this.getRoleIds((String) loginId);

        List<String> permissionIds = rolePermissionService.list(
                        new LambdaQueryWrapper<RolePermission>()
                                .in(RolePermission::getRoleId, roleIds)
                                .eq(RolePermission::getStatus,1)
                                .select(RolePermission::getPermissionId))
                .stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());


        return permissionService.list(
                        new LambdaQueryWrapper<Permission>()
                                .in(Permission::getId,permissionIds)
                                .eq(Permission::getStatus,1)
                                .select(Permission::getPermissionName))
                .stream()
                .map(Permission::getPermissionName)
                .collect(Collectors.toList());


    }


    public List<String> getRoleList(Object loginId) {

        List<String> roleIds = this.getRoleIds((String) loginId);

        if (roleIds.isEmpty())return new ArrayList<>();

        return roleService.list(
                        new LambdaQueryWrapper<Role>()
                                .in(Role::getId, roleIds)
                                .eq(Role::getStatus,1)
                                .eq(Role::getStatus,1))
                .stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

    }


    public List<String> getRoleIds(String loginId){
        List<UserRole> list = userRoleService.list(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, loginId)
                        .eq(UserRole::getStatus,1)
                        .select(UserRole::getRoleId));


        return list.stream().map(UserRole::getRoleId).collect(Collectors.toList());
    }
}
