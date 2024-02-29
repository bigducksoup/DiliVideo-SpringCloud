package com.ducksoup.dilivideotranscoding.controller.param.MinIORelated;


import com.ducksoup.dilivideotranscoding.entity.CallBack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MinIOSourceTranscodeParam {

    private CallBack callBack;

    private String transcodeServiceName;

    @Valid
    private MinIODownLoadMeta minIODownLoadMeta;

    @Valid
    private List<QualityAndMinIOUploadMeta> qualityAndMinIOUploadMetaList;


}
