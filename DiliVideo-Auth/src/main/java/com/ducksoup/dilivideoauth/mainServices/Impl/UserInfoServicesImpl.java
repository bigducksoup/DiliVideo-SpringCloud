package com.ducksoup.dilivideoauth.mainServices.Impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideoauth.controller.Params.UserInfoUpdateParams;
import com.ducksoup.dilivideoauth.entity.MUser;
import com.ducksoup.dilivideoauth.mainServices.UserInfoService;
import com.ducksoup.dilivideoauth.service.MUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;



@Service
public class UserInfoServicesImpl implements UserInfoService {


    @Resource
    private MUserService userService;

    @Override
    public boolean updateUserBasicInfo(UserInfoUpdateParams params,String userId) {
        DateTime time = null;
        if (params.getBirthDay()!=null){
            time = DateUtil.parse(params.getBirthDay(), "yyyy-MM-dd");
        }

        //更新基本信息
        boolean update = userService.update(new LambdaUpdateWrapper<MUser>()
                .eq(MUser::getId, userId)
                .set(params.getName() != null, MUser::getNickname, params.getName())
                .set(params.getSummary() != null, MUser::getSummary, params.getSummary())
                .set(time != null, MUser::getBirthday, time)
                .set(params.getGender() != null, MUser::getGender, params.getGender())
        );

        return update;
    }
}
