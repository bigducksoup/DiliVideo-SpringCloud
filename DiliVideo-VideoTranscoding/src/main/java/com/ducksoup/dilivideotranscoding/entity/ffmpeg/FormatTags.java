package com.ducksoup.dilivideotranscoding.entity.ffmpeg;

import lombok.Data;

@Data
public class FormatTags {
    private String majorBrand;
    private String minorVersion;
    private String compatibleBrands;
    private String encoder;
    private String description;
}