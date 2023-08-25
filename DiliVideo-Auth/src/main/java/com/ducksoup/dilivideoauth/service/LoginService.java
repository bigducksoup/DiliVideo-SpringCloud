package com.ducksoup.dilivideoauth.service;

import com.ducksoup.dilivideoauth.controller.Params.LoginByEmailParam;
import com.ducksoup.dilivideoentity.vo.LoginUserInfoVo;

public interface LoginService {

    LoginUserInfoVo loginbyemail(LoginByEmailParam param);

}
