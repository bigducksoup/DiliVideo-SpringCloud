package com.ducksoup.dilivideoauth.service;

import com.ducksoup.dilivideoauth.Controller.Params.LoginByEmailParam;
import com.ducksoup.dilivideoentity.vo.LoginUserInfoVo;
import org.springframework.stereotype.Service;

public interface LoginService {

    LoginUserInfoVo loginbyemail(LoginByEmailParam param);

}
