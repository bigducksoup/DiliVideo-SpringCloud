package com.ducksoup.dilivideomain.Controller.Params;

import lombok.Data;

@Data
public class PostCommentParams {

    //评论内容
    private String content;

    //评论视频信息id
    private String postId;
}
