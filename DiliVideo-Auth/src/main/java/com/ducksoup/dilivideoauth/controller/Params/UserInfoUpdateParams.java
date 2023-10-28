package com.ducksoup.dilivideoauth.controller.Params;


import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
public class UserInfoUpdateParams {

    @Size(min = 3,max = 20,message = "name长度在3-20个字符之间")
    private String name;


    private String birthDay;

    @Max(1)
    @Min(0)
    private Integer gender;

    @Size(max = 100)
    private String summary;

}
