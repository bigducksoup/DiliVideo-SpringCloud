package com.ducksoup.dilivideoentity.contentEntity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class Videofile implements Serializable {
    /**
     * 主键
     */

    private String id;

    /**
     * 原始名称
     */
    private String originName;

    /**
     * 唯一名称
     */
    private String uniqueName;

    /**
     * 相对于桶的路径
     */
    private String path;

    /**
     * 桶Id
     */
    private String bucket;

    /**
     * 上传时间
     */
    private Date uploadTime;

    /**
     * 对应视频信息Id
     */
    private String videoinfoId;

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

    /**
     * md5
     */
    private String md5;


}