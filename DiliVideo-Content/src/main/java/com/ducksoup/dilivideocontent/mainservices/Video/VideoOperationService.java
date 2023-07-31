package com.ducksoup.dilivideocontent.mainservices.Video;

import com.ducksoup.dilivideocontent.controller.Params.VideoInfoForm;
import com.ducksoup.dilivideocontent.entity.Videofile;

public interface VideoOperationService {

    boolean saveVideo(VideoInfoForm videoInfoForm, Videofile videofile);

    boolean deleteVideo(String videoInfoId);

}
