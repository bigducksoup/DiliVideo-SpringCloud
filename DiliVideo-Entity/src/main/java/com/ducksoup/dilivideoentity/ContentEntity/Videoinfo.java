package com.ducksoup.dilivideoentity.ContentEntity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName ct_videoinfo
 */

@Data
public class Videoinfo implements Serializable {
    /**
     * 主键ID
     */

    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 用户名
     */

    private String authorName;

    /**
     * 作者ID
     */
    private String authorid;

    /**
     * 简介
     */
    private String summary;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 观看数
     */
    private Long watchCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 是否可用
     */
    private Integer status;

    /**
     * 是否发布
     */
    private Integer isPublish;

    /**
     * 是否开启评论
     */
    private Integer openComment;



    private String coverId;

    /**
     * 视频文件ID
     */
    private String videofileId;

    /**
     * 是否原创
     */
    private Integer isOriginal;


    /**
     * 分区id
     */
    private String partitionId;

}