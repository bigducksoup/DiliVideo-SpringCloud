package com.ducksoup.dilivideolive.entity;


import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LiveInfo {

    private String roomId;

    private String userId;

    private List<LivePlayUrls> livePlayUrlSet = new ArrayList<>();

    private DateTime startTime;

}
