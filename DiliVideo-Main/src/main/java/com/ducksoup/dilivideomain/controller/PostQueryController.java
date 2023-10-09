package com.ducksoup.dilivideomain.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.UserVo;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import com.ducksoup.dilivideomain.mainservices.PostQueryService;
import com.ducksoup.dilivideomain.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post_query")
public class PostQueryController {


    @Autowired
    private PostQueryService postQueryService;





    @GetMapping("/query_by_userId")
    public ResponseResult<List<PostVo>> queryPostById(@RequestParam String userId,@RequestParam Integer page,@RequestParam boolean video_only){


        List<PostVo> postVos = postQueryService.getPostByUserId(userId, page,video_only);


        return new ResponseResult<>(HttpStatus.HTTP_OK,"查询成功",postVos);
    }


    @GetMapping("/count")
    public ResponseResult<Long> getUserPostCount(String userId){

        long userPostCount = postQueryService.getUserPostCount(userId);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"查询成功",userPostCount);

    }


    @GetMapping("/query_by_id")
    public ResponseResult<PostVo> queryPostById(@RequestParam String postId){
        PostVo postVo = postQueryService.queryPostById(postId);

        if (postVo == null){
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"查询失败",null);
        }
        return new ResponseResult<>(HttpStatus.HTTP_OK,"查询成功",postVo);
    }


    /**
     * 根据关注列表查询Post
     * @param video_only 是否只查询视频动态
     * @param page 分页
     * @param pageSize 分页大小
     * @return ResponseResult<List<PostVo>>
     */
    @SaCheckLogin
    @GetMapping("/query_by_follow")
    public ResponseResult<List<PostVo>> queryPostByFollow(@RequestParam boolean video_only, @RequestParam Integer page,@RequestParam Integer pageSize){


        String loginId = (String) StpUtil.getLoginId();

        List<PostVo> postVos = postQueryService.queryFollowPost(loginId,page,pageSize,video_only);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取成功",postVos);
    }



}
