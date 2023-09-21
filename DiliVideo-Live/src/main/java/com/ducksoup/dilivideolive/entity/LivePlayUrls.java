package com.ducksoup.dilivideolive.entity;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LivePlayUrls {

    private String type;

    private List<String> SD = new ArrayList<>();

    private List<String> HD = new ArrayList<>();

    private List<String> FHD = new ArrayList<>();

}
