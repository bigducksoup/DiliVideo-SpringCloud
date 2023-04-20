package com.ducksoup.dilivideomain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName m_comment
 */
@TableName(value ="m_comment")
@Data
public class Comment implements Serializable {
    /**
     * 评论Id
     */
    @TableId
    private String id;

    /**
     * 评论人昵称
     */
    private String userNickname;

    /**
     * 评论人头像地址
     */
    private String userAvatarUrl;

    /**
     * 评论时间
     */
    private Date createTime;

    /**
     * 评论人Id
     */
    private String userId;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 是否可用
     */
    private Integer status;

    /**
     * 用户等级
     */
    private Integer userLevel;

    private String content;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}