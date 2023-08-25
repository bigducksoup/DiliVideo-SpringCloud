package com.ducksoup.dilivideomain.controller.params;

import lombok.Data;

@Data
public class ReplyPostCommentParams {

    private String fatherId;

    private String replyToId;

    private String content;

    private String postId;

}
