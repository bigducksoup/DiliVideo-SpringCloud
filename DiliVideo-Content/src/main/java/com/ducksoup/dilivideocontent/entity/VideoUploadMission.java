package com.ducksoup.dilivideocontent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName ct_video_upload_mission
 */
@TableName(value ="ct_video_upload_mission")
@Data
public class VideoUploadMission implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 
     */
    private String fileType;

    /**
     * 
     */
    private Integer size;

    /**
     * 
     */
    private Integer total;

    /**
     * 
     */
    private String platform;

    /**
     * 
     */
    private String md5;

    /**
     * 
     */
    private String userId;

    /**
     * 
     */
    private Integer status;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Integer stateCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}