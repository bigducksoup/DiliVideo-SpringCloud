package com.ducksoup.dilivideotext.controller.params;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class ReplyPostCommentParams {

    @NotNull
    private String fatherId;

    @NotNull
    private String replyToId;

    @Length(min = 5,max = 100,message = "评论内容长度为5-100")
    private String content;

    @NotNull
    private String postId;

}
