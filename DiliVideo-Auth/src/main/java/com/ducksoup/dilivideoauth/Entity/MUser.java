package com.ducksoup.dilivideoauth.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName m_user
 */
@TableName(value ="m_user")
@Data
public class MUser implements Serializable {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

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
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 1男0女3私密
     */
    private Integer gender;

    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 更改时间
     */
    private Date updateTime;

    /**
     * ip
     */
    private String loginIp;

    /**
     * 微信unionID
     */
    private String wechatId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}