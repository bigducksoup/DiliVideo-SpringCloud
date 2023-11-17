package com.ducksoup.dilivideocontent.mainservices.Video.Impl;

import com.ducksoup.dilivideocontent.entity.Tag;
import com.ducksoup.dilivideocontent.mainservices.Video.VideoTagService;
import com.ducksoup.dilivideocontent.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class VideoTagServiceImpl implements VideoTagService {

    final TagService tagService;



    @Override
    public List<Tag> getTagsByVideoInfoId(String videoInfoId) {

        return  tagService.getTagsByVideoInfoId(videoInfoId);

    }
}
