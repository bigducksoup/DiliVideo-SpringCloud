package com.ducksoup.dilivideomain.vo;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostCommentVo {
    /**
     * 评论Id
     */
    @TableId
    private String id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论人昵称
     */
    private String userNickname;

    private String userAvatarPath;

    private String userAvatarBucket;

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

    private List<CommentChild> child;
}
