package com.ducksoup.dilivideotranscoding.entity;

import lombok.Getter;

@Getter
public enum VideoQuality {
    LOW("800k"),
    MEDIUM("2000k"),
    HIGH("4000k"),
    ORIGINAL(null);

    public final String bitRate;

    VideoQuality(String bitRate) {
        this.bitRate = bitRate;
        ConstantContext.putVideoQuality(bitRate, this);
    }

}