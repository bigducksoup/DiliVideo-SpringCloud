package com.ducksoup.dilivideocontent.mainservices.Video;

import com.ducksoup.dilivideocontent.Controller.Params.VideoInfoForm;
import com.ducksoup.dilivideocontent.Entity.Videofile;

public interface VideoOperationService {

    boolean saveVideo(VideoInfoForm videoInfoForm, Videofile videofile);

    boolean deleteVideo(String videoInfoId);

}
