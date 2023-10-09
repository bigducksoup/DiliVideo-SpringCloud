package com.ducksoup.dilivideomain.vo;

import lombok.Data;

import java.util.Date;


@Data
public class PostVo {

    private String id;

    /**
     * 内容模块id
     */
    private String moduleId;

    /**
     * 话题id
     */
    private String topicId;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 分享数
     */
    private Integer shareCount;
    /**
     * 发布时间
     */
    private Date createTime;
    /**
     * 发布状态
     */
    private Integer status;


    private ModuleVo moduleVO;







}
