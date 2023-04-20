package com.ducksoup.dilivideomain.Controller.Params;


import lombok.Data;

@Data
public class ReplyCommentParams {

    private String fatherId;

    private String replyToId;

    private String content;

    private Integer ifDirect;

}
