package com.ducksoup.dilivideocontent.mainservices.UserOperation;


public interface LikeOperationService {

    void likeVideo(String userId, String videoInfoId);

    boolean updateLikeCount(String videoInfoId);

    void unlikeVideo(String userId,String videoInfoId);

    boolean checkLikeVideo(String userId,String videoInfoId);

}
