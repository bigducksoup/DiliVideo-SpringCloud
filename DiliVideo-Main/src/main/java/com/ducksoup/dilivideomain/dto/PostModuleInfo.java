package com.ducksoup.dilivideomain.dto;

import com.ducksoup.dilivideoentity.AuthEntity.MUser;
import lombok.Data;

@Data
public class PostModuleInfo {

    private MUser userInfo;

    private String typeId;

    private String desc;

    private String videoInfoId;

    private String childPostmoduleId;
}
