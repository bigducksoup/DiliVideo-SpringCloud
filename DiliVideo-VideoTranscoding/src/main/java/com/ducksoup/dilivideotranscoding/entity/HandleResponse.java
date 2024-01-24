package com.ducksoup.dilivideotranscoding.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HandleResponse {
    private boolean ok;

    private String message;

    private String Object;

    private String region;

    private String bucket;

    private String format;

    private String quality;
}