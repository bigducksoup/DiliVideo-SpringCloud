package com.ducksoup.dilivideoentity.admin.params;

import lombok.Data;

@Data
public class PermissionGrantParam {

    private String roleId;

    private String permissionId;

    private String note;
}
