package com.ducksoup.dilivideoauth.service;

import com.ducksoup.dilivideoauth.entity.MUser;
import com.ducksoup.dilivideoauth.entity.RelationFollow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【relation_follow】的数据库操作Service
* @createDate 2023-04-27 09:37:49
*/
public interface RelationFollowService extends IService<RelationFollow> {

    boolean follow(String userId,String followId);

    boolean unfollow(String userId,String followId);

    List<MUser> getFollowList(String userId);

    List<MUser> getFollowerList(String userId);

}
