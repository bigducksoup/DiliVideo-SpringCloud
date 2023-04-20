package com.ducksoup.dilivideomain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName m_comment_videoinfo
 */
@TableName(value ="m_comment_videoinfo")
@Data
public class CommentVideoinfo implements Serializable {
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
     * 视频信息Id
     */
    private String videoinfoId;

    /**
     * 是否可用
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}