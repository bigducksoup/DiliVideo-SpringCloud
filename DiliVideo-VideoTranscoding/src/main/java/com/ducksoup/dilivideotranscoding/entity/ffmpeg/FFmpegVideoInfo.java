package com.ducksoup.dilivideotranscoding.entity.ffmpeg;

import lombok.Data;

import java.util.List;

@Data
public class FFmpegVideoInfo {
    private List<Stream> streams;
    private Format format;
}