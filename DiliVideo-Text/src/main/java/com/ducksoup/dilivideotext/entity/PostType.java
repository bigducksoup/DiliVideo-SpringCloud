package com.ducksoup.dilivideotext.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName post_type
 */
@TableName(value ="post_type")
@Data
public class PostType implements Serializable {
    /**
     * 动态类型id
     */
    @TableId
    private String id;

    /**
     * 动态类型名称
     */
    private String name;

    /**
     * 动态类型标识
     */
    private String mark;

    /**
     * 状态
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}