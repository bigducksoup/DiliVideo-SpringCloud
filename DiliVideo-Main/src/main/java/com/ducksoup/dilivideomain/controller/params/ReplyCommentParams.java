package com.ducksoup.dilivideomain.controller.params;


import lombok.Data;

@Data
public class ReplyCommentParams {

    private String fatherId;

    private String replyToId;

    private String content;

    private Integer ifDirect;

}
