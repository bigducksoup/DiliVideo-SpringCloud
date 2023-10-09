package com.ducksoup.dilivideoauth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName user_settings
 */
@TableName(value ="user_settings")
@Data
public class UserSettings implements Serializable {
    /**
     * 主键
     */
    private String id;

    /**
     * setting所属用户id
     */
    private String userId;

    /**
     * 是否公开关注 0：否 1:是
     */
    private Integer followPublic;

    /**
     * 是否公开粉丝 0：否 1:是
     */
    private Integer followerPublic;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}