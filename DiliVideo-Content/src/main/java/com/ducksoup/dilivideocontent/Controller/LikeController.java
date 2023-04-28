package com.ducksoup.dilivideocontent.Controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideocontent.Controller.Params.LikeParams;
import com.ducksoup.dilivideocontent.mainservices.UserOperation.LikeOperationService;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 视频点赞控制器
 */
@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeOperationService likeOperationService;


    /**
     * 点赞视频
     * @param params videoInfoId
     * @return ResponseResult<Boolean>
     */

    @SaCheckLogin
    @PostMapping("/like_video")
    public ResponseResult<Boolean> likeVideo(@RequestBody LikeParams params){

        String loginId = (String) StpUtil.getLoginId();

        likeOperationService.likeVideo(loginId,params.getVideoInfoId());

        return new ResponseResult<>(HttpStatus.HTTP_OK,"点赞成功",true);

    }

    /**
     * 取消点赞
     * @param params videoInfoId
     * @return ResponseResult<Boolean>
     */

    @SaCheckLogin
    @PostMapping("/unlike_video")
    public ResponseResult<Boolean> unlikeVideo(@RequestBody LikeParams params){

        String loginId = (String) StpUtil.getLoginId();

        likeOperationService.unlikeVideo(loginId,params.getVideoInfoId());

        return new ResponseResult<>(HttpStatus.HTTP_OK,"取消点赞成功",true);

    }

    /**
     * 查看是否点赞
     * @param videoInfoId 视频信息id
     * @return ResponseResult<Boolean>
     */

    @SaCheckLogin
    @GetMapping("/check")
    public ResponseResult<Boolean> checkLike(String videoInfoId){
        String loginId = (String) StpUtil.getLoginId();

        boolean likeVideo = likeOperationService.checkLikeVideo(loginId, videoInfoId);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"查询成功",likeVideo);

    }



}
