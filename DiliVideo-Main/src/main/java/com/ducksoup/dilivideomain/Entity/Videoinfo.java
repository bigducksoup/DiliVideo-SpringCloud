package com.ducksoup.dilivideomain.Entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 
 * @TableName m_videoinfo
 */
@TableName(value ="m_videoinfo")
@Data
public class Videoinfo implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private String id;

    /**
     * 标题
     */
    private String title;

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
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}