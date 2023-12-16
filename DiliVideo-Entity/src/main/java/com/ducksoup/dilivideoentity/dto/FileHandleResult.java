package com.ducksoup.dilivideoentity.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FileHandleResult implements Serializable {

    // missionId
    // videoInfoId
    // mission status
    // origin file path
    // origin file bucket
    // md5
    // size
    // List of transcode file info
    //  ---bucket
    //  ---path
    //  ---file type
    //  ---md5
    //  ---w
    //  ---h
    //  ---quality
    //  ---size
    // chunks paths
    // chunks bucket


    private String missionId;

    private String videoInfoId;

    private Integer fileStatus;

    private String originFilePath;

    private String originFileBucket;

    private String md5;

    private Long size;

    private List<TranscodeFileInfo> transcodeFileInfos;

    private String chunkBucket;

    private List<String> chunkPaths;

    private String coverBucket;

    private String coverPath;


}



