package com.ducksoup.dilivideomain.Controller.Params;

import lombok.Data;

@Data
public class ReplyPostCommentParams {

    private String fatherId;

    private String replyToId;

    private String content;

    private String postId;

}
