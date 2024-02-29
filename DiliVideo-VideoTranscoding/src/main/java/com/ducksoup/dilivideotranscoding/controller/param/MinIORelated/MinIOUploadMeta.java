package com.ducksoup.dilivideotranscoding.controller.param.MinIORelated;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class MinIOUploadMeta {

    @NotNull
    private String uploadBucket;

    private String uploadRegion;

    @NotNull
    private String uploadObject;

    private String uploadContentType;

    private Map<String,String> uploadExtraHeaders;

    private Map<String,String> uploadExtraQueryParams;

    private Map<String,String> uploadHeaders;

}
