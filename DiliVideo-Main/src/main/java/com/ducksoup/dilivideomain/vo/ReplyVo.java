package com.ducksoup.dilivideomain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ReplyVo {

    private String id;

    private String replierId;

    private String avatar;

    private String replierName;

    private Integer level;

    private String toId;

    private String toName;

    private String content;

    private Date time;

    private Integer likeCount;

}
