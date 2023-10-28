package com.ducksoup.dilivideolive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName live_control_info
 */
@TableName(value ="live_control_info")
@Data
public class LiveControlInfo implements Serializable {
    /**
     * key
     */
    @TableId
    private String id;

    /**
     * room id
     */
    private String liveRoomId;

    /**
     * 
     */
    private String addr;

    /**
     * 
     */
    private String call;

    /**
     * use this to control stream
     */
    private String clientId;

    /**
     * application name
     */
    private String app;

    /**
     * 
     */
    private String flashVer;

    private String swfUrl;

    private String tcUrl;

    private String pageUrl;

    private String name;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}