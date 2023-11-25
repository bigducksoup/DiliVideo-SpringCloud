package com.ducksoup.dilivideotext.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName post_topic
 */
@TableName(value ="post_topic")
@Data
public class PostTopic implements Serializable {
    /**
     * 话题id
     */
    @TableId
    private String id;

    /**
     * 话题名称
     */
    private String name;

    /**
     * 话题描述
     */
    private String description;

    /**
     * 话题发布时间
     */
    private Date createTime;

    /**
     * 状态
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}