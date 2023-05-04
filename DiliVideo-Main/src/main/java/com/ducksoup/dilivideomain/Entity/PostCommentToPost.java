package com.ducksoup.dilivideomain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName post_comment_to_post
 */
@TableName(value ="post_comment_to_post")
@Data
public class PostCommentToPost implements Serializable {
    /**
     * Id
     */
    @TableId
    private String id;

    /**
     * 评论id
     */
    private String commentId;

    /**
     * 动态Id
     */
    private String postId;

    /**
     * 是否可用
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}