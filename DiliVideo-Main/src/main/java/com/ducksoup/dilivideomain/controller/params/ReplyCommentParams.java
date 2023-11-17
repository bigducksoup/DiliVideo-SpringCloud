package com.ducksoup.dilivideomain.controller.params;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class ReplyCommentParams {

    @NotNull
    private String fatherId;

    @NotNull
    private String replyToId;

    @Length(min = 5,max = 300,message = "评论长度不能小于5，且不能超过300")
    private String content;


    private Integer ifDirect;

}
