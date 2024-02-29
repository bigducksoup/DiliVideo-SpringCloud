package com.ducksoup.dilivideotranscoding.entity;


import com.ducksoup.dilivideotranscoding.entity.ffmpeg.Stream;
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

    public AudioStreamInfo(Stream stream){
        this.codecName = stream.getCodecName();
        this.duration = Double.parseDouble(stream.getDuration());
        this.bitRate = Long.parseLong(stream.getBitRate());
        this.channels = Math.toIntExact(stream.getChannels());
        this.codecTag = stream.getCodecTag();
    }
    
}
