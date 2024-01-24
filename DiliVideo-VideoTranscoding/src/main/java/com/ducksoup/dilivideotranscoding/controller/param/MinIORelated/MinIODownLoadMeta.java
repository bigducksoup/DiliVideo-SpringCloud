package com.ducksoup.dilivideotranscoding.controller.param.MinIORelated;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class MinIODownLoadMeta {

    @NotNull
    private String downloadBucket;

    @NotNull
    private String downloadObjectName;

    private String downloadRegion;

    private Map<String,String> downloadExtraHeaders;

    private Map<String,String> downloadExtraQueryParams;

}
