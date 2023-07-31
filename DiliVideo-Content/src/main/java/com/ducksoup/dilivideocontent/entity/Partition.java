package com.ducksoup.dilivideocontent.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName ct_partition
 */
@TableName(value ="ct_partition")
@Data
public class Partition implements Serializable {
    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 分区名称
     */
    private String partitionname;

    /**
     * 分区描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updataTime;

    /**
     * 是否可用
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}