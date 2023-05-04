package com.ducksoup.dilivideomain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName post_module
 */
@TableName(value ="post_module")
@Data
public class PostModule implements Serializable {
    /**
     * 动态内容Id
     */
    @TableId
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 描述
     */
    private String desc;

    /**
     * 分类id
     */
    private String typeId;

    /**
     * 视频信息id
     */
    private String videoInfoId;

    /**
     * 转发的动态内容id
     */
    private String childPostmoduleId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}