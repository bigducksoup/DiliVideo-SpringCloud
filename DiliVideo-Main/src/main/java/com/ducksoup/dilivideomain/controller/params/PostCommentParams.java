package com.ducksoup.dilivideomain.controller.params;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class PostCommentParams {

    @Length(min = 5, max = 100,message = "评论内容长度为5-100")
    //评论内容
    private String content;

    @NotNull
    //评论视频信息id
    private String postId;
}
