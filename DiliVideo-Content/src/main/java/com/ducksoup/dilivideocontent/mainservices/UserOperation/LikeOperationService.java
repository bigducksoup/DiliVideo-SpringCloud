package com.ducksoup.dilivideocontent.mainservices.UserOperation;


public interface LikeOperationService {

    Long likeVideo(String userId, String videoInfoId);

    Long updateLikeCount(String videoInfoId);

    Long unlikeVideo(String userId,String videoInfoId);

    boolean checkLikeVideo(String userId,String videoInfoId);

}
