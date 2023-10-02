package com.ducksoup.dilivideotranscoding.entity;


import lombok.Data;
import net.bramp.ffmpeg.probe.FFmpegStream;

@Data
public class VideoStreamInfo {


    private int width;

    private int height;

    private long bitRate;

    private double duration;


    private String codec_name;

    private Integer ratioX;

    private Integer ratioY;
    
    public VideoStreamInfo(){
        
    }
    
    
    public VideoStreamInfo(FFmpegStream videoStream){
        this.width = videoStream.width;
        this.height = videoStream.height;
        this.bitRate = videoStream.bit_rate;
        this.duration = videoStream.duration;
        this.codec_name = videoStream.codec_name;
        String[] ratio = videoStream.display_aspect_ratio.split(":");
        this.ratioX = Integer.parseInt(ratio[0]);
        this.ratioY = Integer.parseInt(ratio[1]);
    }




}
