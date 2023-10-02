package com.ducksoup.dilivideocontent.feign;


import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideocontent.entity.Videofile;
import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.ducksoup.dilivideocontent.service.VideofileService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.OSSUtils;
import com.ducksoup.dilivideoentity.content.FileInfoUpdateParam;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feign/video_info")
public class FeignVideoInfoController {

    @Autowired
    private VideoinfoService videoinfoService;

    @Autowired
    private VideofileService videofileService;

    @Autowired
    private OSSUtils ossUtils;

    @GetMapping("/getById")
    public ResponseResult<Videoinfo> getVideoInfoById(@RequestParam String videoInfoId){

        Videoinfo videoinfo = videoinfoService.getById(videoInfoId);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"success",videoinfo);
    }

    @PostMapping("/update_file_info")
    public boolean setStatus(@RequestBody FileInfoUpdateParam fileStatusUpdateParam) {

        Videofile videofile = videofileService.getById(fileStatusUpdateParam.getVideoFileId());


        String path = videofile.getPath();
        String uniqueName = videofile.getUniqueName();


        String newPath = path.split("\\.")[0]+".mp4";
        String newUniqueName = uniqueName.split("\\.")[0]+".mp4";
        String newFullPath = ossUtils.makeUrl(videofile.getBucket(),newPath);

        videofile.setPath(newPath);
        videofile.setUniqueName(newUniqueName);
        videofile.setFullpath(newFullPath);

        videofileService.updateById(videofile);


        return videoinfoService.update(
                new LambdaUpdateWrapper<Videoinfo>()
                        .eq(Videoinfo::getId,fileStatusUpdateParam.getVideoInfoId())
                        .set(Videoinfo::getStatus,fileStatusUpdateParam.getStatus()));

    }

}
