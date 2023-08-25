package com.ducksoup.dilivideoauth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName auth_role
 */
@TableName(value ="auth_role")
@Data
public class Role implements Serializable {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 
     */
    private String roleName;

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

    /**
     * 
     */
    private String note;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}