package com.ducksoup.dilivideocontent.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName ct_cover
 */
@TableName(value ="ct_cover")
@Data
public class Cover implements Serializable {
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