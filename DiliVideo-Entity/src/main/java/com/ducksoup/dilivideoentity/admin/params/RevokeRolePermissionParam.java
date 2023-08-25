package com.ducksoup.dilivideoentity.admin.params;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class RevokeRolePermissionParam {

    private String roleId;

    private String permissionId;

    @Max(1)
    @Min(0)
    private Integer mode;

}
