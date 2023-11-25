package com.ducksoup.dilivideotext.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;

import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.result.ResponseResult;

import com.ducksoup.dilivideotext.controller.params.ForwardTextPostParams;
import com.ducksoup.dilivideotext.controller.params.ForwardVideoParams;
import com.ducksoup.dilivideotext.controller.params.TextPostParams;
import com.ducksoup.dilivideotext.mainservices.PostOperationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {


    @Autowired
    private PostOperationService postOperationService;


    /**
     * 发布动态（文字）
     * @param postParams TextPostParams
     * @return ResponseResult<Boolean>
     */

    @SaCheckLogin
    @PostMapping("/text")
    public ResponseResult<Boolean> postText(TextPostParams postParams){
        try {
            Boolean saveTextPost = postOperationService.saveTextPost(postParams);

            return saveTextPost?
                    new ResponseResult<>(HttpStatus.HTTP_OK,"发布成功",true)
                    :
                    new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"发布失败",false);

        } catch (Exception e) {
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"发布失败",false);
        }

    }


    /**
     * 转发他人文字动态
     * @param params ForwardTextPostParams
     * @return ResponseResult<Boolean>
     */
    @SaCheckLogin
    @PostMapping("/forward/text")
    public ResponseResult<Boolean> forwardText(ForwardTextPostParams params){

        try {
            Boolean forwardTextPost = postOperationService.saveForwardTextPost(params);

            return forwardTextPost?
                    new ResponseResult<>(HttpStatus.HTTP_OK,"发布成功",true)
                    :
                    new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"发布失败",false);

        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"发布失败",false);
        }

    }

    /**
     * 转发视频动态
     * @param params ForwardVideoParams
     * @return ResponseResult<Boolean>
     */
    @SaCheckLogin
    @PostMapping("/forward/video")
    public ResponseResult<Boolean> forwardVideo(ForwardVideoParams params){

        try {
            boolean forwardVideoPost = postOperationService.saveForwardVideoPost(params);

            return forwardVideoPost?
                    new ResponseResult<>(HttpStatus.HTTP_OK,"发布成功",true)
                    :
                    new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"发布失败",false);

        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"发布失败",false);
        }

    }


    @SaCheckLogin
    @PostMapping("/delete")
    public ResponseResult<Boolean> deletePost(String postId){

        boolean delete = postOperationService.deletePost(postId);
        if (delete)return new ResponseResult<>(HttpStatus.HTTP_OK,"删除成功",true);


        return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"删除失败",false);


    }
























}
