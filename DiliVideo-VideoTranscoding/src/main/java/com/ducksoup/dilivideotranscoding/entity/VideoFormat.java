package com.ducksoup.dilivideotranscoding.entity;

import lombok.Getter;

@Getter
public enum VideoFormat {

    MP4("mp4"),
    FLV("flv"),
    MOV("mov"),
    AVI("avi"),

    MKV("mkv");

    public final String value;

    VideoFormat(String value){
        this.value = value;
        ConstantContext.putVideoFormat(value,this);
    }

}
