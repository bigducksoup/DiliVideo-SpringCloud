package com.ducksoup.dilivideotranscoding.entity.ffmpeg;

import cn.hutool.core.annotation.Alias;
import lombok.Data;
import lombok.Value;

@Data
public class Disposition {
    @Alias("default")
    private long dispositionDefault;
    private long dub;
    private long original;
    private long comment;
    private long lyrics;
    private long karaoke;
    private long forced;
    private long hearingImpaired;
    private long visualImpaired;
    private long cleanEffects;
    private long attachedPic;
    private long timedThumbnails;
    private long captions;
    private long descriptions;
    private long metadata;
    private long dependent;
    private long stillImage;
}