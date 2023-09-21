package com.ducksoup.dilivideolive.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName live_server
 */
@TableName(value ="live_server")
@Data
public class LiveServer implements Serializable {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 服务器名称
     */
    private String name;

    /**
     * 服务器ip
     */
    private String ip;

    /**
     * 服务器域名
     */
    private String domain;

    /**
     * 服务器live端口
     */
    private Integer port;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 服务器是否可用
     */
    private Integer status;

    /**
     * 服务器连接数
     */
    private Integer connectionCount;

    /**
     * 核心数
     */
    private Integer cores;

    /**
     * 内存大小（单位MB）
     */
    private Integer memory;

    /**
     * 存储大小（单位MB）
     */
    private Integer storage;

    /**
     * 带宽（单位 Mbps）
     */
    private Integer bandwidth;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}