package com.ducksoup.dilivideocontent.mainservices.UserOperation;

import com.ducksoup.dilivideoentity.vo.VideoInfoVo;

import java.util.List;


/**
 * 视频播放量相关操作
 */
public interface ViewsService {


    Long addViews(String videoInfoId);


    void setVideoViewCount(VideoInfoVo videoInfoVo);

    void setVideoListViewCount(List<VideoInfoVo> videoInfoVos);

}
