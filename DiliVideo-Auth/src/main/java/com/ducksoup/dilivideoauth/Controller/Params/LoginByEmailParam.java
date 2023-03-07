package com.ducksoup.dilivideoauth.Controller.Params;

import lombok.Data;

@Data
public class LoginByEmailParam {

    private String email;

    private String password;

    private String ip;

    private Long timestamp;

}
