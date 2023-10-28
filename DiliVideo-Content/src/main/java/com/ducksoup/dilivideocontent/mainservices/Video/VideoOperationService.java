package com.ducksoup.dilivideocontent.mainservices.Video;

import com.ducksoup.dilivideocontent.controller.Params.VideoInfoForm;
import com.ducksoup.dilivideocontent.entity.Videofile;

import java.util.List;

public interface VideoOperationService {

    boolean saveVideo(VideoInfoForm videoInfoForm, Videofile videofile);

    boolean deleteVideo(String videoInfoId);

    boolean saveVideoTagInfo(String videoInfoId, List<String> tagIds);

}
