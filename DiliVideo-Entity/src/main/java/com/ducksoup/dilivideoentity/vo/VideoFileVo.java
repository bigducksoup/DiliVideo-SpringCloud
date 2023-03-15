package com.ducksoup.dilivideoentity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class VideoFileVo {


    private String id;

    /**
     * 唯一名称
     */
    private String uniqueName;



    /**
     * 上传时间
     */
    private Date uploadTime;



    /**
     * 视频文件大小
     */
    private Long size;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 文件全路径
     */
    private String fullpath;




}
