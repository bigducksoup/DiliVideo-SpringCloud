package com.ducksoup.dilivideoentity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TranscodeFileInfo implements Serializable {

    private String bucket;

    private String path;

    private String fileType;

    private String md5;

    private Integer w;

    private Integer h;

    private String quality;

    private Long size;
}