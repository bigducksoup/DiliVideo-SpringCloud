package com.ducksoup.dilivideotranscoding.entity.ffmpeg;

import lombok.Data;

@Data
public class Stream {
    private long index;
    private String codecName;
    private String codecLongName;
    private String profile;
    private String codecType;
    private String codecTagString;
    private String codecTag;
    private Long width;
    private Long height;
    private Long codedWidth;
    private Long codedHeight;
    private Long closedCaptions;
    private Long filmGrain;
    private Long hasBFrames;
    private String sampleAspectRatio;
    private String displayAspectRatio;
    private String pixFmt;
    private Long level;
    private String chromaLocation;
    private String fieldOrder;
    private Long refs;
    private String isavc;
    private String nalLengthSize;
    private String id;
    private String rFrameRate;
    private String avgFrameRate;
    private String timeBase;
    private long startPts;
    private String startTime;
    private long durationTs;
    private String duration;
    private String bitRate;
    private String bitsPerRawSample;
    private String nbFrames;
    private long extradataSize;
    private Disposition disposition;
    private StreamTags tags;
    private String sampleFmt;
    private String sampleRate;
    private Long channels;
    private String channelLayout;
    private Long bitsPerSample;
    private Long initialPadding;
}