package com.ducksoup.dilivideomain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName post_imgs
 */
@TableName(value ="post_imgs")
@Data
public class PostImgs implements Serializable {
    /**
     * 图片文件
     */
    @TableId
    private String id;

    /**
     * 
     */
    private String originalName;

    /**
     * 
     */
    private String uniqueName;

    /**
     * 
     */
    private String path;

    /**
     * 
     */
    private String moduleId;

    /**
     * 
     */
    private String bucket;

    /**
     * 
     */
    private Date uploadTime;

    /**
     * 
     */
    private Long size;

    /**
     * 
     */
    private Integer state;

    /**
     * 
     */
    private String fullpath;

    /**
     * 
     */
    private String md5;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}