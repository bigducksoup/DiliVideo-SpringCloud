package com.ducksoup.dilivideomain.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideofeign.Content.ContentServices;
import com.ducksoup.dilivideomain.Controller.Params.TextPostParams;
import com.ducksoup.dilivideomain.mainservices.PostOperationService;
import com.ducksoup.dilivideomain.service.PostImgsService;
import com.ducksoup.dilivideomain.service.PostModuleService;
import com.ducksoup.dilivideomain.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {


    @Autowired
    private PostOperationService postOperationService;

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
