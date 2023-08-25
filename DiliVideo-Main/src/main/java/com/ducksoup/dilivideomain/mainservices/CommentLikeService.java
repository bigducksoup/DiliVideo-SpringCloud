package com.ducksoup.dilivideomain.mainservices;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideomain.entity.Comment;
import com.ducksoup.dilivideomain.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;



@Service
public class CommentLikeService {


    @Autowired
    private CommentService commentService;



    @Async(value = "ThreadPool")
    public void updateCommentLikeCount(String commentId,Long count){

        commentService.update(new LambdaUpdateWrapper<Comment>().eq(Comment::getId,commentId).set(Comment::getLikeCount,count));

    }

}
