package com.ducksoup.dilivideoauth.mainServices;

import com.ducksoup.dilivideoauth.controller.Params.UserInfoUpdateParams;

public interface UserInfoService {

    boolean updateUserBasicInfo(UserInfoUpdateParams params,String userId);

}
