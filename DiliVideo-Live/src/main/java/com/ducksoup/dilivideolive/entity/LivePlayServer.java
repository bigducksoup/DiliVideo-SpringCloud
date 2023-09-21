package com.ducksoup.dilivideolive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName live_play_server
 */
@TableName(value ="live_play_server")
@Data
public class LivePlayServer implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * application name
     */
    private String applicationName;

    /**
     * server ip
     */
    private String ip;

    /**
     * application port
     */
    private String port;

    /**
     * application domain
     */
    private String domain;

    /**
     * connection count
     */
    private Integer connectionCount;

    /**
     * if available
     */
    private Integer status;

    /**
     * enable hls?
     */
    private Integer hls;


    private String httpProtocol;

    /**
     * http port to access m3u8 file
     */
    private String httpPort;

    /**
     * http path to access m3u8 file
     */
    private String httpPath;

    /**
     * stream source server
     */
    private String pushServerId;

    /**
     * 
     */
    private Date createTime;

    private String protocol;

    //higher better 1,2,3
    private Integer quality;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}