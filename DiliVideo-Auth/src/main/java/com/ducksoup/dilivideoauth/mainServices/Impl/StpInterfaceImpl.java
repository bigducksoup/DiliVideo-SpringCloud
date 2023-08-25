package com.ducksoup.dilivideoauth.mainServices.Impl;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideoauth.entity.Permission;
import com.ducksoup.dilivideoauth.entity.Role;
import com.ducksoup.dilivideoauth.entity.RolePermission;
import com.ducksoup.dilivideoauth.entity.UserRole;
import com.ducksoup.dilivideoauth.service.PermissionService;
import com.ducksoup.dilivideoauth.service.RolePermissionService;
import com.ducksoup.dilivideoauth.service.RoleService;
import com.ducksoup.dilivideoauth.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class StpInterfaceImpl implements StpInterface {


    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private PermissionService permissionService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {

        List<String> roleIds = this.getRoleIds((String) loginId);

        List<String> permissionIds = rolePermissionService.list(
                new LambdaQueryWrapper<RolePermission>()
                        .in(RolePermission::getRoleId, roleIds)
                        .select(RolePermission::getPermissionId))
                .stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());


       return permissionService.list(
               new LambdaQueryWrapper<Permission>()
                       .in(Permission::getId,permissionIds)
                       .select(Permission::getPermissionName))
               .stream()
               .map(Permission::getPermissionName)
               .collect(Collectors.toList());


    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {

        List<String> roleIds = this.getRoleIds((String) loginId);

        return roleService.list(
                new LambdaQueryWrapper<Role>()
                        .in(Role::getId, roleIds)
                        .eq(Role::getStatus,1))
                .stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

    }


    private List<String> getRoleIds(String loginId){
        List<UserRole> list = userRoleService.list(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, loginId)
                        .select(UserRole::getRoleId));


        return list.stream().map(UserRole::getRoleId).collect(Collectors.toList());
    }
}
