package com.ducksoup.dilivideobarrage.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName ba_bullet
 */
@TableName(value ="ba_bullet")
@Data
public class BaBullet implements Serializable {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 内容
     */
    private String content;

    /**
     * 颜色
     */
    private Integer color;

    /**
     * 出现时间（秒）
     */
    private Integer appearTimeSesonds;

    /**
     * 发送时间
     */
    private Date createTime;

    /**
     * 视频信息ID
     */
    private String videoinfoId;

    /**
     * 发送用户Id
     */
    private String userId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否居中
     */
    private Integer ifmid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}