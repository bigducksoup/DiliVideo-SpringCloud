package com.ducksoup.dilivideoauth.mainServices;

import com.ducksoup.dilivideoauth.entity.MUser;
import com.ducksoup.dilivideoentity.vo.UserVo;

import java.util.List;

public interface DesensitizationService {

    List<UserVo> desensitizeUserInfo(List<MUser> users);

}
