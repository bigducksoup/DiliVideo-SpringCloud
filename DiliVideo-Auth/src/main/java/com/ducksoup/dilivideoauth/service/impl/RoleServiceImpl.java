package com.ducksoup.dilivideoauth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideoauth.entity.Role;
import com.ducksoup.dilivideoauth.service.RoleService;
import com.ducksoup.dilivideoauth.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author meichuankutou
* @description 针对表【auth_role】的数据库操作Service实现
* @createDate 2023-03-04 13:40:48
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




