package com.ducksoup.dilivideomain.mainservices;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideomain.entity.PostComment;
import com.ducksoup.dilivideomain.service.PostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class PostCommentLikeService {


    @Autowired
    private PostCommentService postCommentService;


    @Async(value = "ThreadPool")
    public void updatePostCommentLikeCount(String postCommentId,Long count){

        postCommentService.update(new LambdaUpdateWrapper<PostComment>().eq(PostComment::getId,postCommentId).set(PostComment::getLikeCount,count));

    }


}
