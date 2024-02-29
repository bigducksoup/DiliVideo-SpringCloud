package com.ducksoup.dilivideotranscoding.entity.ffmpeg;

import lombok.Data;

@Data
public class Format {
    private String filename;
    private long nbStreams;
    private long nbPrograms;
    private String formatName;
    private String formatLongName;
    private String startTime;
    private String duration;
    private String size;
    private String bitRate;
    private long probeScore;
    private FormatTags tags;
}