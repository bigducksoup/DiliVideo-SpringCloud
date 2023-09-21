package com.ducksoup.dilivideolive.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName live_category
 */
@TableName(value ="live_category")
@Data
public class LiveCategory implements Serializable {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标id
     */
    private String iconId;

    /**
     * bannerID
     */
    private String bannerId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}