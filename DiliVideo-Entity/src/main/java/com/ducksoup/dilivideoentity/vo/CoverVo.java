package com.ducksoup.dilivideoentity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CoverVo {

    private String id;



    private String uniqueName;


    private Date uploadTime;


    private Long size;


    private Integer state;


    private String fullpath;

}
