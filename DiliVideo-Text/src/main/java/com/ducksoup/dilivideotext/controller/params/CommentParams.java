package com.ducksoup.dilivideotext.controller.params;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class CommentParams {


    @Length(min = 5,max = 300,message = "评论长度不能小于5，且不能超过300")
    //评论内容
    private String content;

    @NotNull
    //评论视频信息id
    private String videoInfoId;


}
