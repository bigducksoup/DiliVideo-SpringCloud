package com.ducksoup.dilivideoauth.mainServices.Impl;

import com.ducksoup.dilivideoauth.entity.MUser;
import com.ducksoup.dilivideoauth.mainServices.DesensitizationService;
import com.ducksoup.dilivideoentity.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据脱敏
 */
@Service
public class DesensitizationServiceImpl implements DesensitizationService {


    /**
     * 脱敏用户信息
     * @param users List<MUser>
     * @return List<UserVo>
     */
    @Override
    public List<UserVo> desensitizeUserInfo(List<MUser> users) {
        List<UserVo> res = new ArrayList<>();

        users.forEach(user ->{
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user,userVo);
            res.add(userVo);
        });

        return res;

    }
}
