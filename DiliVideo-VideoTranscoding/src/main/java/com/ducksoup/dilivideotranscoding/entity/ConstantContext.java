package com.ducksoup.dilivideotranscoding.entity;

import java.util.HashMap;

public class ConstantContext {

    public static HashMap<String,VideoFormat> videoFormatMap = new HashMap<>();

    public static HashMap<String,VideoQuality> videoQualityMap = new HashMap<>();

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
