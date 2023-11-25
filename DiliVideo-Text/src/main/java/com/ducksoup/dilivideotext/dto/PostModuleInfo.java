package com.ducksoup.dilivideotext.dto;

import com.ducksoup.dilivideoentity.auth.MUser;
import lombok.Data;

@Data
public class PostModuleInfo {

    private MUser userInfo;

    private String typeId;

    private String desc;

    private String videoInfoId;

    private String childPostId;
}
