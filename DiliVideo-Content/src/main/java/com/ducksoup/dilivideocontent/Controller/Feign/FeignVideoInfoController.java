package com.ducksoup.dilivideocontent.Controller.Feign;


import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideocontent.Entity.Videoinfo;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign/video_info")
public class FeignVideoInfoController {

    @Autowired
    private VideoinfoService videoinfoService;

    @GetMapping("/getById")
    public ResponseResult<Videoinfo> getVideoInfoById(@RequestParam String videoInfoId){

        Videoinfo videoinfo = videoinfoService.getById(videoInfoId);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"success",videoinfo);
    }

}
