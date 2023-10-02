package com.ducksoup.dilivideotranscoding.entity;


import lombok.Data;
import net.bramp.ffmpeg.probe.FFmpegStream;

@Data
public class AudioStreamInfo {
    
    private String codecName;
    
    private double duration;
    
    private long bitRate;
    
    private int channels;
    
    private String codecTag;
    public AudioStreamInfo(){
        
    }
    
    public AudioStreamInfo(FFmpegStream audioStream){

        this.codecName = audioStream.codec_name;
        this.duration = audioStream.duration;
        this.bitRate = audioStream.bit_rate;
        this.channels = audioStream.channels;
        this.codecTag = audioStream.codec_tag;
        
    }
    
}
