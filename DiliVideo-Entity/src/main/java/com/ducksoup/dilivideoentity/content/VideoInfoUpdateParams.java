package com.ducksoup.dilivideoentity.content;


import lombok.Data;

@Data
public class VideoInfoUpdateParams {

    /**
     * 主键ID
     */

    private String id;


    /**
     * 标题
     */
    private String title;

    /**
     * 用户名
     */

    private String authorName;


    /**
     * 简介
     */
    private String summary;


    /**
     * 是否可用
     */
    private Integer status;



    /**
     * 是否开启评论
     */
    private Integer openComment;

    /**
     * 是否原创
     */
    private Integer isOriginal;


    /**
     * 分区id
     */
    private String partitionId;


}
