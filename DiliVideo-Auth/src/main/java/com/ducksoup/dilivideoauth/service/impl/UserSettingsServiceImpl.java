package com.ducksoup.dilivideoauth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideoauth.entity.UserSettings;
import com.ducksoup.dilivideoauth.service.UserSettingsService;
import com.ducksoup.dilivideoauth.mapper.UserSettingsMapper;
import org.springframework.stereotype.Service;

/**
* @author meichuankutou
* @description 针对表【user_settings】的数据库操作Service实现
* @createDate 2023-10-03 22:17:44
*/
@Service
public class UserSettingsServiceImpl extends ServiceImpl<UserSettingsMapper, UserSettings>
    implements UserSettingsService{

}




