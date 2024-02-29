package com.ducksoup.dilivideotranscoding.service.compisition.result;

import com.ducksoup.dilivideotranscoding.controller.param.MinIORelated.MinIOUploadMeta;
import com.ducksoup.dilivideotranscoding.entity.VideoFileInfo;
import lombok.Data;

@Data
public class MinIOSourceCombineResult {

    private Boolean ok;

    private MinIOUploadMeta uploadMeta;

    private String msg;

    private VideoFileInfo videoFileInfo;

}
