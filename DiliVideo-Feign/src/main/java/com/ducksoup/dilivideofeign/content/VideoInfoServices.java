package com.ducksoup.dilivideofeign.content;


import com.ducksoup.dilivideoentity.content.VideoAuthorInfoUpdateParams;
import com.ducksoup.dilivideoentity.content.VideoInfoUpdateParams;
import com.ducksoup.dilivideoentity.dto.FileHandleResult;
import com.ducksoup.dilivideofeign.Inteceptor.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "DiliVideo-Content",configuration = FeignInterceptor.class)
public interface VideoInfoServices {


    @PostMapping("/feign/video_info/update_video_info")
    Boolean updateVideoInfo(@RequestBody VideoInfoUpdateParams params);

    @PostMapping("/feign/video_info/update_video_author_info")
    Boolean updateVideoAuthorInfo(@RequestBody VideoAuthorInfoUpdateParams params);


    @PostMapping("/feign/video_info/transcoding_done")
    void onTranscodingDone(@RequestBody FileHandleResult fileHandleResult);

}
