package com.ducksoup.dilivideoadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName role_application
 */
@TableName(value ="role_application")
@Data
public class RoleApplication implements Serializable {
    /**
     * 主键id
     */
    @TableId
    private String id;

    /**
     * 申请人id
     */
    private String applicantId;

    /**
     * 角色Name
     */
    private String roleName;

    /**
     * 时间
     */
    private Date createTime;

    /**
     * 当前状态
     */
    private Integer currentStatus;

    /**
     * 通用状态
     */
    private Integer status;

    /**
     * 申请缘由
     */
    private String reason;

    /**
     * 批示
     */
    private String note;

    /**
     * 处理人id
     */
    private String processedBy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}