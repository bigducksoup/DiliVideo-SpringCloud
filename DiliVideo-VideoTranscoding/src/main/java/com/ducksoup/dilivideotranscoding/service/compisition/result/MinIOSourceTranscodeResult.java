package com.ducksoup.dilivideotranscoding.service.compisition.result;


import com.ducksoup.dilivideotranscoding.entity.HandleResponse;
import lombok.Data;

import java.util.List;

@Data
public class MinIOSourceTranscodeResult {


    private String object;

    private String region;

    private String bucket;

    private String message;

    private int code;

    List<HandleResponse> handleResponses;

}
