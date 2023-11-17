package com.ducksoup.dilivideolive.mainservices;

import com.ducksoup.dilivideoentity.admin.params.RoleApplicationParam;
import com.ducksoup.dilivideoentity.constant.CONSTANT_ROLE;
import com.ducksoup.dilivideofeign.admin.RoleServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RoleApplicationService  {


    @Autowired
    private RoleServices roleServices;

    /**
     * 申请主播权限
     * @param userId 请求者Id
     * @return boolean
     */

    public boolean replyForAnchorRole(String userId) {

        RoleApplicationParam roleApplicationParam = new RoleApplicationParam();
        roleApplicationParam.setApplicantId(userId);
        roleApplicationParam.setRoleName(CONSTANT_ROLE.ANCHOR);
        roleApplicationParam.setReason("系统发出，主播权限申请");
        return roleServices.RoleApplicationAction(roleApplicationParam);

    }
}
