package com.ducksoup.dilivideoadmin.mainservices;

import cn.dev33.satoken.stp.StpInterface;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StpInterfaceImpl implements StpInterface {


    @Autowired
    private AuthServices authServices;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return authServices.getPermissionsByLoginId((String) loginId);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return authServices.getRolesByLoginId((String) loginId);
    }


}
