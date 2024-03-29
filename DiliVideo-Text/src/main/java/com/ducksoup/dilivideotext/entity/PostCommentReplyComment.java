package com.ducksoup.dilivideotext.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName post_comment_reply_comment
 */
@TableName(value ="post_comment_reply_comment")
@Data
public class PostCommentReplyComment implements Serializable {
    /**
     * Id
     */
    @TableId
    private String id;

    /**
     * 父评论id
     */
    private String fatherCommentId;

    /**
     * 被回复评论Id
     */
    private String commentId;

    /**
     * 回复评论Id
     */
    private String replyCommentId;

    /**
     * 是否直属顶级评论
     */
    private Integer ifdirect;

    /**
     * 是否可用
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}