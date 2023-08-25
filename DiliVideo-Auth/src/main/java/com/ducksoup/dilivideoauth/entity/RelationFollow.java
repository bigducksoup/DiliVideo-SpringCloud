package com.ducksoup.dilivideoauth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName relation_follow
 */
@TableName(value ="relation_follow")
@Data
public class RelationFollow implements Serializable {
    /**
     * 关系id
     */
    @TableId
    private String id;



    /**
     * 用户id
     */
    private String userId;

    /**
     * 关注用户头像地址
     */
    private String fllowAvatarurl;

    /**
     * 关注昵称
     */
    private String followNickname;

    /**
     * 关注id
     */
    private String followId;

    /**
     * 
     */
    private Date createTime;

    /**
     * 状态
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}