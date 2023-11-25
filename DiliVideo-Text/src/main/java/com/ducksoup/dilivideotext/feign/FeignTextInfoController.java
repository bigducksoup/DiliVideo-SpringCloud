package com.ducksoup.dilivideotext.feign;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideoentity.main.TextUserInfoUpdateParams;
import com.ducksoup.dilivideotext.entity.Comment;
import com.ducksoup.dilivideotext.entity.PostComment;
import com.ducksoup.dilivideotext.service.CommentService;
import com.ducksoup.dilivideotext.service.PostCommentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/feign/text_info")
public class FeignTextInfoController {


    @Resource
    private CommentService commentService;


    @Resource
    private PostCommentService postCommentService;

    @PostMapping("/update_user_info")
    public  Boolean updateUserInfo(@RequestBody TextUserInfoUpdateParams params){
        // update user nickname in comment and post comment

        boolean a = commentService.update(new LambdaUpdateWrapper<Comment>().eq(Comment::getUserId, params.getUserId())
                .set(params.getNickName() != null, Comment::getUserNickname, params.getNickName())
        );

        boolean b = postCommentService.update(new LambdaUpdateWrapper<PostComment>().eq(PostComment::getUserId, params.getUserId())
                .set(params.getNickName() != null, PostComment::getUserNickname, params.getNickName())
        );

        return a && b;
    }



}
