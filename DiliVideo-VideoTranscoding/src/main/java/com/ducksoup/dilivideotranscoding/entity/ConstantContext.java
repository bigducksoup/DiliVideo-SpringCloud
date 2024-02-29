package com.ducksoup.dilivideotranscoding.entity;

import java.util.HashMap;

public class ConstantContext {

    public static HashMap<String,VideoFormat> videoFormatMap = new HashMap<>();

    public static HashMap<String,VideoQuality> videoQualityMap = new HashMap<>();


    static {
        putVideoQuality(VideoQuality.HIGH.getBitRate(), VideoQuality.HIGH);
        putVideoQuality(VideoQuality.LOW.getBitRate(), VideoQuality.LOW);
        putVideoQuality(VideoQuality.MEDIUM.getBitRate(), VideoQuality.MEDIUM);
        putVideoQuality(VideoQuality.ORIGINAL.getBitRate(), VideoQuality.ORIGINAL);

        putVideoFormat(VideoFormat.MP4.value, VideoFormat.MP4);
        putVideoFormat(VideoFormat.MOV.value, VideoFormat.MOV);
        putVideoFormat(VideoFormat.MKV.value, VideoFormat.MKV);
        putVideoFormat(VideoFormat.FLV.value, VideoFormat.FLV);
        putVideoFormat(VideoFormat.AVI.value, VideoFormat.AVI);
    }

    public static void putVideoFormat(String name,VideoFormat videoFormat){
        videoFormatMap.put(name,videoFormat);
    }

    public static void putVideoQuality(String name,VideoQuality videoQuality){
        videoQualityMap.put(name,videoQuality);
    }

    public static VideoFormat getVideoFormat(String name){
        return videoFormatMap.get(name);
    }

    public static VideoQuality getVideoQuality(String name){
        return videoQualityMap.get(name);
    }

}
