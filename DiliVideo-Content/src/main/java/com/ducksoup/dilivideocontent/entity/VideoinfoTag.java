package com.ducksoup.dilivideocontent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName ct_videoInfo_tag
 */
@TableName(value ="ct_videoInfo_tag")
@Data
public class VideoinfoTag implements Serializable {
    /**
     * key
     */
    @TableId
    private String id;

    /**
     * 视频信息id
     */
    private String videoInfoId;

    /**
     * 标签Id
     */
    private String tagId;

    /**
     * 标签no
     */
    private Integer tagNo;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}