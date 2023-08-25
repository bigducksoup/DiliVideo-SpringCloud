package com.ducksoup.dilivideoauth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideoauth.entity.UserRole;
import com.ducksoup.dilivideoauth.service.UserRoleService;
import com.ducksoup.dilivideoauth.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

/**
* @author meichuankutou
* @description 针对表【auth_user_role】的数据库操作Service实现
* @createDate 2023-03-04 13:40:48
*/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService{

}




