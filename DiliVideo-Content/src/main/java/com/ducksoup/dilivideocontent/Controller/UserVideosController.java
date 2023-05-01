package com.ducksoup.dilivideocontent.Controller;


import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user_videos")
public class UserVideosController {

    @Autowired
    private VideoinfoService videoinfoService;

    @GetMapping("/published")
    public ResponseResult<List<VideoInfoVo>> getUserPublishedVideos(@RequestParam String userId,int page){

        List<VideoInfoVo> videoInfoVoList = videoinfoService.getPublishedVideoById(userId, page, 20);


        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取成功",videoInfoVoList);


    }




}
