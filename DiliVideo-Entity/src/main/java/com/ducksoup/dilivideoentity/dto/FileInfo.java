package com.ducksoup.dilivideoentity.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class FileInfo implements Serializable {


    /**
     * 
     */
    private String originalName;



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
    private String fullpath;

    /**
     * 
     */
    private String md5;

}