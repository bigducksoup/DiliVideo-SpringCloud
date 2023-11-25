package com.ducksoup.dilivideocontent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChunkUploadInfo {

    private Boolean exists;

    private Integer index;

    private String url;

    private String md5;


}
