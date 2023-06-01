package com.ducksoup.dilivideomain.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;

import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.Result.ResponseResult;

import com.ducksoup.dilivideomain.Controller.Params.TextPostParams;
import com.ducksoup.dilivideomain.mainservices.PostOperationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




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














}
