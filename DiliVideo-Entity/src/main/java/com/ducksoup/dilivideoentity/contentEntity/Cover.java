package com.ducksoup.dilivideoentity.contentEntity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class Cover implements Serializable {
    /**
     * 图片文件
     */

    private String id;

    /**
     * 
     */
    private String originalName;

    /**
     * 
     */
    private String uniqueName;

    /**
     * 
     */
    private String path;


    /**
     * 
     */
    private String bucket;

    /**
     * 
     */
    private Date uploadTime;

    /**
     * 
     */
    private Long size;

    /**
     * 
     */
    private Integer state;

    /**
     * 
     */
    private String fullpath;

    /**
     * 
     */
    private String md5;

}