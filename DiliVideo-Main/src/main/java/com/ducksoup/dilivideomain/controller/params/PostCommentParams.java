package com.ducksoup.dilivideomain.controller.params;

import lombok.Data;

@Data
public class PostCommentParams {

    //评论内容
    private String content;

    //评论视频信息id
    private String postId;
}
