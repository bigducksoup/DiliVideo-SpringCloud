package com.ducksoup.dilivideoentity.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName m_user
 */

@Data
public class UserVo implements Serializable {
    /**
     * ID
     */

    private String id;

    /**
     * 昵称
     */
    private String nickname;




    /**
     * 昵称地址
     */
    private String avatarUrl;

    /**
     * 签名
     */
    private String summary;

    /**
     * 粉丝数
     */
    private Long followerCount;

    /**
     * 关注数
     */
    private Long followedCount;

    /**
     * 投稿数
     */
    private Integer publishCount;

    /**
     * 是否封禁
     */
    private Integer isBaned;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 经验值
     */
    private Integer exp;



    /**
     * 生日
     */
    private Date birthday;

    /**
     * 1男0女3私密
     */
    private Integer gender;

}