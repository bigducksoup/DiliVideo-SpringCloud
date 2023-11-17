package com.ducksoup.dilivideocontent.mainservices.UserOperation;


import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;

import java.util.List;
import java.util.Map;

public interface LikeOperationService {

    Long likeVideo(String userId, String videoInfoId);

    Long updateLikeCount(String videoInfoId);

    Long unlikeVideo(String userId,String videoInfoId);

    boolean checkLikeVideo(String userId,String videoInfoId);


    /**
     * 设置videoInfoVo的点赞状态
     * @param videoInfoVos List<VideoInfoVo>
     */
    void setVideoLikeStatus(List<VideoInfoVo> videoInfoVos);

    /**
     * 设置videoInfoVo的点赞状态
     * @param videoInfoVo VideoInfoVo
     */
    void setVideoLikeStatus(VideoInfoVo videoInfoVo);

}
