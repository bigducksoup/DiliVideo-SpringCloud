package com.ducksoup.dilivideoentity.ContentEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName ct_videofile
 */
public class CtVideofile implements Serializable {
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
    private String bucketid;

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

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 主键
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 原始名称
     */
    public String getOriginName() {
        return originName;
    }

    /**
     * 原始名称
     */
    public void setOriginName(String originName) {
        this.originName = originName;
    }

    /**
     * 唯一名称
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * 唯一名称
     */
    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    /**
     * 相对于桶的路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 相对于桶的路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 桶Id
     */
    public String getBucketid() {
        return bucketid;
    }

    /**
     * 桶Id
     */
    public void setBucketid(String bucketid) {
        this.bucketid = bucketid;
    }

    /**
     * 上传时间
     */
    public Date getUploadTime() {
        return uploadTime;
    }

    /**
     * 上传时间
     */
    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    /**
     * 对应视频信息Id
     */
    public String getVideoinfoId() {
        return videoinfoId;
    }

    /**
     * 对应视频信息Id
     */
    public void setVideoinfoId(String videoinfoId) {
        this.videoinfoId = videoinfoId;
    }

    /**
     * 视频文件大小
     */
    public Long getSize() {
        return size;
    }

    /**
     * 视频文件大小
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * 状态
     */
    public Integer getState() {
        return state;
    }

    /**
     * 状态
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 文件全路径
     */
    public String getFullpath() {
        return fullpath;
    }

    /**
     * 文件全路径
     */
    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CtVideofile other = (CtVideofile) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOriginName() == null ? other.getOriginName() == null : this.getOriginName().equals(other.getOriginName()))
            && (this.getUniqueName() == null ? other.getUniqueName() == null : this.getUniqueName().equals(other.getUniqueName()))
            && (this.getPath() == null ? other.getPath() == null : this.getPath().equals(other.getPath()))
            && (this.getBucketid() == null ? other.getBucketid() == null : this.getBucketid().equals(other.getBucketid()))
            && (this.getUploadTime() == null ? other.getUploadTime() == null : this.getUploadTime().equals(other.getUploadTime()))
            && (this.getVideoinfoId() == null ? other.getVideoinfoId() == null : this.getVideoinfoId().equals(other.getVideoinfoId()))
            && (this.getSize() == null ? other.getSize() == null : this.getSize().equals(other.getSize()))
            && (this.getState() == null ? other.getState() == null : this.getState().equals(other.getState()))
            && (this.getFullpath() == null ? other.getFullpath() == null : this.getFullpath().equals(other.getFullpath()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOriginName() == null) ? 0 : getOriginName().hashCode());
        result = prime * result + ((getUniqueName() == null) ? 0 : getUniqueName().hashCode());
        result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
        result = prime * result + ((getBucketid() == null) ? 0 : getBucketid().hashCode());
        result = prime * result + ((getUploadTime() == null) ? 0 : getUploadTime().hashCode());
        result = prime * result + ((getVideoinfoId() == null) ? 0 : getVideoinfoId().hashCode());
        result = prime * result + ((getSize() == null) ? 0 : getSize().hashCode());
        result = prime * result + ((getState() == null) ? 0 : getState().hashCode());
        result = prime * result + ((getFullpath() == null) ? 0 : getFullpath().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", originName=").append(originName);
        sb.append(", uniqueName=").append(uniqueName);
        sb.append(", path=").append(path);
        sb.append(", bucketid=").append(bucketid);
        sb.append(", uploadTime=").append(uploadTime);
        sb.append(", videoinfoId=").append(videoinfoId);
        sb.append(", size=").append(size);
        sb.append(", state=").append(state);
        sb.append(", fullpath=").append(fullpath);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}