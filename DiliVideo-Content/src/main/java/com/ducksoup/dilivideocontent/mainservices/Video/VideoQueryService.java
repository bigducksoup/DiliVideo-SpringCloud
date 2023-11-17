package com.ducksoup.dilivideocontent.mainservices.Video;

import com.ducksoup.dilivideoentity.vo.VideoInfoVo;

import java.util.List;

public interface VideoQueryService {

    List<VideoInfoVo> getRecommend(Integer page);


    List<VideoInfoVo> getRelatedVideo(String videoInfoId);

}
