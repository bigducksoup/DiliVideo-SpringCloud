package com.ducksoup.dilivideotranscoding.controller.param.MinIORelated;

import com.ducksoup.dilivideotranscoding.entity.VideoFormat;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;


@Data
public class MinIOSourceFileCombineParam {

    @Valid
    private List<MinIODownLoadMeta> chunkMetas;

    private String ownerId;

    private String format;

    @Valid
    private MinIOUploadMeta uploadMeta;
}
