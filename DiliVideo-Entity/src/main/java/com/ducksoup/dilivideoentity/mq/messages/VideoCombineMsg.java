package com.ducksoup.dilivideoentity.mq.messages;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class VideoCombineMsg implements Serializable {


    private String videoInfoId;

    private String missionId;

    private String loginId;

    private List<String> objectNames;

    private String bucket;

    private String fileType;

    private String destinationBucket;

    private String destinationObjectName;

}
