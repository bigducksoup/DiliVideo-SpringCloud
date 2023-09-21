package com.ducksoup.dilivideoentity.admin.params;


import lombok.Data;

@Data
public class RoleApplicationParam {

    /**
     * 申请人id
     */
    private String applicantId;


    /**
     * 角色id
     */
    private String roleName;


    /**
     * 申请缘由
     */
    private String reason;

}
