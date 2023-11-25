package com.ducksoup.dilivideocontent.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName ct_video_chunk
 */
@TableName(value ="ct_video_chunk")
@Data
public class VideoChunk implements Serializable {
    /**
     * 分片id
     */
    @TableId
    private String id;

    /**
     * chunk unique name
     */
    private String uniqueName;

    /**
     * chunk original name
     */
    private String originalName;

    /**
     * chunk code in redis
     */
    private String code;

    /**
     * chunk upload time
     */
    private Date uploadTime;

    private String chunkPath;

    private String chunkBucket;


    private Integer chunkIndex;

    /**
     * chunk md5
     */
    private String md5;

    /**
     * if chunk is used for combination
     */
    private Integer used;

    /**
     * how many count the file is sliced
     */
    private Integer totalChunkCount;

    private String missionId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}