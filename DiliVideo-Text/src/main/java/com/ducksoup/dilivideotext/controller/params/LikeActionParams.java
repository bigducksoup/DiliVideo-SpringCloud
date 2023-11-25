package com.ducksoup.dilivideotext.controller.params;


import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class LikeActionParams {


    @Min(value = 0)
    @Max(value = 2)
    private Integer targetType;

    private String targetId;

}
