package com.ducksoup.dilivideoauth.controller.Params;

import lombok.Data;

@Data
public class RegisterParam {



    private String password;

    private String nickname;

    private String code;

    private String avatarCode;

    private Integer gender;

}
