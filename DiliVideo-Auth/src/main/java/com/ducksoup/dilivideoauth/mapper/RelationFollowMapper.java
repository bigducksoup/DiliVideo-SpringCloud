package com.ducksoup.dilivideoauth.mapper;

import com.ducksoup.dilivideoauth.entity.MUser;
import com.ducksoup.dilivideoauth.entity.RelationFollow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【relation_follow】的数据库操作Mapper
* @createDate 2023-04-27 09:37:49
* @Entity com.ducksoup.dilivideoauth.Entity.RelationFollow
*/
public interface RelationFollowMapper extends BaseMapper<RelationFollow> {

    List<MUser> getFollowList(@Param("userId") String userId);

    List<MUser> getFansList(@Param("userId") String userId);

}




