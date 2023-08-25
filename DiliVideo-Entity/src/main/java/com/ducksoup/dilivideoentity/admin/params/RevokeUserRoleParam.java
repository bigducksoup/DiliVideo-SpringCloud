package com.ducksoup.dilivideoentity.admin.params;


import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class RevokeUserRoleParam {

    private String userId;

    private String roleId;

    @Max(1)
    @Min(0)
    private Integer mode;

}
