package com.ducksoup.dilivideoauth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideoauth.entity.Permission;
import com.ducksoup.dilivideoauth.service.PermissionService;
import com.ducksoup.dilivideoauth.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

/**
* @author meichuankutou
* @description 针对表【auth_permission】的数据库操作Service实现
* @createDate 2023-03-04 13:40:48
*/
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission>
    implements PermissionService{

}




