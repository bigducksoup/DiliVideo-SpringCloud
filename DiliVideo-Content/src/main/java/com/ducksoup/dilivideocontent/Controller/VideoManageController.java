package com.ducksoup.dilivideocontent.Controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideocontent.Controller.Params.VideoDeleteParams;
import com.ducksoup.dilivideocontent.Controller.Params.VideoInfoForm;
import com.ducksoup.dilivideocontent.Entity.Videofile;
import com.ducksoup.dilivideocontent.dto.FileTransmissionInfo;
import com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl.UploadService;
import com.ducksoup.dilivideocontent.mainservices.Rabbit.NotifyVideoFFMPEG;
import com.ducksoup.dilivideocontent.mainservices.Video.VideoOperationService;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/video")
public class VideoManageController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private VideoOperationService videoOperationService;

    @Autowired
    private NotifyVideoFFMPEG notifyVideoFFMPEG;

    @SaCheckLogin
    @PostMapping("/upload")
    public ResponseResult<String> uploadVideo( FileTransmissionInfo fileTransmissionInfo){

        try {
            String s = uploadService.uploadVideo(fileTransmissionInfo.getFile(), fileTransmissionInfo.getCode());
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

        boolean b = videoOperationService.deleteVideo(params.getVideoInfoId());

        if (b){
            return new ResponseResult<>(HttpStatus.HTTP_OK,"删除成功", true);
        }else {
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"删除失败",false);
        }

    }




}
