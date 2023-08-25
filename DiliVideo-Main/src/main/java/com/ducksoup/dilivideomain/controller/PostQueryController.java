package com.ducksoup.dilivideomain.controller;


import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideomain.mainservices.PostQueryService;
import com.ducksoup.dilivideomain.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/post_query")
public class PostQueryController {


    @Autowired
    private PostQueryService postQueryService;



    @GetMapping("/query_by_userId")
    public ResponseResult<List<PostVo>> queryPostById(@RequestParam String userId,@RequestParam Integer page){


        List<PostVo> postVos = postQueryService.getPostByUserId(userId, page);


        return new ResponseResult<>(HttpStatus.HTTP_OK,"查询成功",postVos);
    }


    @GetMapping("/count")
    public ResponseResult<Long> getUserPostCount(String userId){

        long userPostCount = postQueryService.getUserPostCount(userId);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"查询成功",userPostCount);

    }


}