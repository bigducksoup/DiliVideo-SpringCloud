package com.ducksoup.dilivideocontent.controller.Params;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UploadChunkParams {

    @NotNull
    private String missionId;

    @NotNull
    private String md5;

    @NotNull
    private Integer index;

    @NotNull
    private Integer total;

    private Integer size;

    private Long timestamp;

    @NotNull
    private String fileName;

}
