package com.ducksoup.dilivideotranscoding.entity;


import lombok.Data;

@Data
public class VideoFileDetail {

    private long size;

    private double duration;

    private VideoStreamInfo videoStreamInfo;

    private AudioStreamInfo audioStreamInfo;
}
