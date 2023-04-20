package com.ducksoup.dilivideoentity.vo;


import com.ducksoup.dilivideoentity.ContentEntity.Cover;
import com.ducksoup.dilivideoentity.ContentEntity.Videoinfo;


import lombok.Data;

import java.util.Date;

@Data
public class VideoInfoVo {


    /**
     * 主键ID
     */
    private String videoInfoId;

    /**
     * 标题
     */
    private String videoAuthorId;

    private String videoAuthorName;

    /**
     * 作者ID
     */
    private Integer collectCount;

    /**
     * 简介
     */
    private Integer commentCount;

    /**
     * 创建时间
     */
    private Date createTime;


    private Integer isOriginal;

    /**
     * 观看数
     */
    private Long watchCount;

    /**
     * 点赞数
     */
    private Long likeCount;



    /**
     * 是否发布
     */
    private Integer isPublish;

    /**
     * 是否开启评论
     */
    private Integer openComment;

    private String title;


    private String summary;

    private String videoFileId;

    private String videoFileUrl;

    private String videoFileName;

    private String coverId;

    private String coverName;

    private String coverUrl;



    private String partitionId;

    public void setAllData(Videoinfo videoinfo, Cover cover){
        this.setVideoInfoId(videoinfo.getId());
        this.setVideoAuthorId(videoinfo.getAuthorid());
        this.setVideoAuthorName(videoinfo.getAuthorName());
        this.setCollectCount(videoinfo.getCollectCount());
        this.setCommentCount(videoinfo.getCommentCount());
        this.setCreateTime(videoinfo.getCreateTime());
        this.setIsOriginal(videoinfo.getIsOriginal());
        this.setWatchCount(videoinfo.getWatchCount());
        this.setLikeCount(videoinfo.getLikeCount());
        this.setIsPublish(videoinfo.getIsPublish());
        this.setOpenComment(videoinfo.getOpenComment());
        this.setTitle(videoinfo.getTitle());
        this.setSummary(videoinfo.getSummary());
        this.setVideoFileId(videoinfo.getVideofileId());
        this.setVideoFileUrl("null");
        this.setVideoFileName("null");
        this.setCoverId(cover.getId());
        this.setCoverName(cover.getUniqueName());
        this.setCoverUrl(cover.getFullpath());
        this.setPartitionId(videoinfo.getPartitionId());
    }



}
