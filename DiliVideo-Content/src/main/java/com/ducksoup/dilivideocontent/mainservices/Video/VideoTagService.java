package com.ducksoup.dilivideocontent.mainservices.Video;

import com.ducksoup.dilivideocontent.entity.Tag;

import java.util.List;

public interface VideoTagService {

    /**
     * query video tag by id
     * @param videoInfoId id of video info
     * @return List<Tag> tags
     *
     */
    List<Tag> getTagsByVideoInfoId(String videoInfoId);



}
