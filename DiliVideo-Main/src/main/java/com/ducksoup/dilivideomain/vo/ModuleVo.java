package com.ducksoup.dilivideomain.vo;

import lombok.Data;

import java.util.List;

@Data
public class ModuleVo {

    /**
     * 动态内容Id
     */

    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户头像地址
     */
    private String userAvatarUrl;

    private String userAvatarPath;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 描述
     */
    private String description;

    /**
     * 分类id
     */
    private String typeId;

    /**
     * 视频信息id
     */
    private String videoInfoId;

    /**
     * 转发的动态内容id
     */
    private String childPostId;

    private List<String> imgs;
}
