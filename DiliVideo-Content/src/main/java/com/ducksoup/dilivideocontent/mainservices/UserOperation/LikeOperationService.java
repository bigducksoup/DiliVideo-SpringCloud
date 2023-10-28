package com.ducksoup.dilivideocontent.mainservices.UserOperation;


import com.ducksoup.dilivideoentity.vo.VideoInfoVo;

import java.util.List;

public interface LikeOperationService {

    Long likeVideo(String userId, String videoInfoId);

    Long updateLikeCount(String videoInfoId);

    Long unlikeVideo(String userId,String videoInfoId);

    boolean checkLikeVideo(String userId,String videoInfoId);

    void setVideoLikeStatus(List<VideoInfoVo> videoInfoVos);

}
