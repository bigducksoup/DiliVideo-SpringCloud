package com.ducksoup.dilivideolive.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName live_room
 */
@TableName(value ="live_room")
@Data
public class LiveRoom implements Serializable {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 房间号
     */
    private Integer roomNumber;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 分类id
     */
    private String categoryId;

    /**
     * 房间名
     */
    private String roomname;

    /**
     * 是否直播
     */
    private Integer isLive;

    /**
     * 房间封面
     */
    private String coverId;

    /**
     * 是否可用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}