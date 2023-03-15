package com.ducksoup.dilivideoentity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class VideoInfoVo {


    /**
     * 主键ID
     */
    private String videoInfoId;

    /**
     * 标题
     */
    private String videoAuthorId;

    private String videoAuthorName;

    /**
     * 作者ID
     */
    private Integer collectCount;

    /**
     * 简介
     */
    private Integer commentCount;

    /**
     * 创建时间
     */
    private Date createTime;


    private Integer isOriginal;

    /**
     * 观看数
     */
    private Long watchCount;

    /**
     * 点赞数
     */
    private Long likeCount;



    /**
     * 是否发布
     */
    private Integer isPublish;

    /**
     * 是否开启评论
     */
    private Integer openComment;

    private String title;


    private String summary;

    private String videoFileId;

    private String videoFileUrl;

    private String videoFileName;

    private String coverId;

    private String coverName;

    private String coverUrl;

    /**
     * 视频文件ID
     */
    private String videofileId;



}
