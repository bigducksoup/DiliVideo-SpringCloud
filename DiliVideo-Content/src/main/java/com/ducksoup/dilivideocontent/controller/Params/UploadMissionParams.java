package com.ducksoup.dilivideocontent.controller.Params;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UploadMissionParams {

    //MB
    private Integer size;

    private Long timestamp;

    @NotNull
    private Integer total;

    private String platform;

    @NotNull
    private String fileType;

    private String md5;

}
