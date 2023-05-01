package com.ducksoup.dilivideocontent.Controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideocontent.Controller.Params.VideoDeleteParams;
import com.ducksoup.dilivideocontent.Controller.Params.VideoInfoForm;
import com.ducksoup.dilivideocontent.Controller.Params.VideoInfoUpdateForm;
import com.ducksoup.dilivideocontent.Entity.Videofile;
import com.ducksoup.dilivideocontent.Entity.Videoinfo;
import com.ducksoup.dilivideocontent.dto.FileTransmissionInfo;
import com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl.UploadServiceImpl;
import com.ducksoup.dilivideocontent.mainservices.Rabbit.NotifyVideoFFMPEG;
import com.ducksoup.dilivideocontent.mainservices.Video.VideoOperationService;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/video")
public class VideoManageController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UploadServiceImpl uploadServiceImpl;

    @Autowired
    private VideoOperationService videoOperationService;

    @Autowired
    private VideoinfoService videoinfoService;


    @Autowired
    private NotifyVideoFFMPEG notifyVideoFFMPEG;

    @Autowired
    private CoverService coverService;

    @SaCheckLogin
    @PostMapping("/upload")
    public ResponseResult<String> uploadVideo( FileTransmissionInfo fileTransmissionInfo){

        try {
            String s = uploadServiceImpl.uploadVideo(fileTransmissionInfo.getFile(), fileTransmissionInfo.getCode());
        } catch (Exception e){
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"上传失败");
        }
        return new ResponseResult<>(HttpStatus.HTTP_OK,"上传成功",fileTransmissionInfo.getCode());
    }

    @SaCheckLogin
    @GetMapping("/getuploadcode")
    public ResponseResult<String> getUploadCode(){

        //获取登录id
        String loginId = (String) StpUtil.getLoginId();

        String randomNumber = RandomUtil.randomNumbers(6);

        while (redisUtil.exists(randomNumber)){
            randomNumber = RandomUtil.randomNumbers(6);
        }


        return new ResponseResult<String>(HttpStatus.HTTP_OK,"获取code成功",randomNumber);
    }

    @PostMapping("/submit_videoInfo_form")
    public ResponseResult<Boolean> submitVideoInfoForm(VideoInfoForm videoInfoForm){

        String code = videoInfoForm.getCode();
        //通过code防止重复提交，且取出上传视频时保存的数据
        Videofile fileinfo = (Videofile) redisUtil.get(code);
        if (fileinfo==null){
            return new ResponseResult<>(HttpStatus.HTTP_GONE,"code无效");
        }
        redisUtil.remove(code);

        //保存视频信息，与上传封面
        boolean saveVideo = videoOperationService.saveVideo(videoInfoForm, fileinfo);

        if (saveVideo){
            notifyVideoFFMPEG.notifyVideoFFMPG(fileinfo);
        }

        return saveVideo ? new ResponseResult<>(HttpStatus.HTTP_OK, "上传完成", true) :
                new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "上传失败", false);
    }


    /**
     * 删除视频
     * @param params 视频信息Id
     * @return ResponseResult<Boolean>
     */
    @SaCheckLogin
    @PostMapping("/delete")
    public ResponseResult<Boolean> deleteVideo(@RequestBody VideoDeleteParams params){


        String loginId = (String) StpUtil.getLoginId();
        Videoinfo videoInfoUserId = videoinfoService.getOne(new LambdaQueryWrapper<Videoinfo>().select(Videoinfo::getAuthorid).eq(Videoinfo::getId, params.getVideoInfoId()));

        if (!loginId.equals(videoInfoUserId.getAuthorid())){
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN,"权限不足",false);
        }


        boolean b = videoOperationService.deleteVideo(params.getVideoInfoId());

        if (b){
            return new ResponseResult<>(HttpStatus.HTTP_OK,"删除成功", true);
        }else {
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"删除失败",false);
        }

    }



    @SaCheckLogin
    @GetMapping("/published_videos")
    public ResponseResult<List<VideoInfoVo>> getOwnVideoList(@RequestParam Integer page){

        String loginId = (String) StpUtil.getLoginId();

        int pageSize = 10;

        List<VideoInfoVo> videoInfoVos = videoinfoService.getPublishedVideoById(loginId, page, pageSize);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"success",videoInfoVos);


    }


    @SaCheckLogin
    @PostMapping("/update_info")
    public ResponseResult<Boolean> updateVideoInfo(@RequestBody VideoInfoUpdateForm form){

        String loginId = (String) StpUtil.getLoginId();
        Videoinfo videoinfo = videoinfoService.getById(form.getId());
        if (!videoinfo.getAuthorid().equals(loginId)){
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN,"无操作权限",false);
        }

        LambdaUpdateWrapper<Videoinfo> updateWrapper = new LambdaUpdateWrapper<Videoinfo>().eq(Videoinfo::getId, form.getId())
                .set(Videoinfo::getTitle, form.getTitle())
                .set(Videoinfo::getPartitionId, form.getPartitionId())
                .set(Videoinfo::getSummary, form.getDescription())
                .set(Videoinfo::getIsOriginal, form.isIforiginal() ? 1 : 0);


        boolean update = videoinfoService.update(updateWrapper);

        return update?
                new ResponseResult<>(HttpStatus.HTTP_OK,"修改成功",true)
                :
                new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"修改失败",false);


    }



}
