package com.ducksoup.dilivideoauth.service;

import com.ducksoup.dilivideoauth.Entity.RelationFollow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author meichuankutou
* @description 针对表【relation_follow】的数据库操作Service
* @createDate 2023-04-27 09:37:49
*/
public interface RelationFollowService extends IService<RelationFollow> {

    boolean follow(String userId,String followId);

    boolean unfollow(String userId,String followId);

}
