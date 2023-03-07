package com.ducksoup.dilivideoauth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideoauth.Entity.MUser;
import com.ducksoup.dilivideoauth.service.MUserService;
import com.ducksoup.dilivideoauth.mapper.MUserMapper;
import org.springframework.stereotype.Service;

/**
* @author meichuankutou
* @description 针对表【m_user】的数据库操作Service实现
* @createDate 2023-03-04 13:40:48
*/
@Service
public class MUserServiceImpl extends ServiceImpl<MUserMapper, MUser>
    implements MUserService{

}




