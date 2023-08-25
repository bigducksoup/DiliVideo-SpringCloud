package com.ducksoup.dilivideoauth.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideoauth.entity.MUser;
import com.ducksoup.dilivideoauth.entity.RelationFollow;
import com.ducksoup.dilivideoauth.service.MUserService;
import com.ducksoup.dilivideoauth.service.RelationFollowService;
import com.ducksoup.dilivideoauth.mapper.RelationFollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author meichuankutou
 * @description 针对表【relation_follow】的数据库操作Service实现
 * @createDate 2023-04-27 09:37:49
 */
@Service
public class RelationFollowServiceImpl extends ServiceImpl<RelationFollowMapper, RelationFollow>
        implements RelationFollowService {

    @Autowired
    private MUserService userService;



    @Transactional
    @Override
    public boolean follow(String userId, String followId) {

        RelationFollow relationFollowInDataBase = this.getOne(
                new LambdaQueryWrapper<RelationFollow>()
                        .eq(RelationFollow::getUserId, userId)
                        .eq(RelationFollow::getFollowId, followId)
        );

        if (relationFollowInDataBase!=null){
            return true;
        }

        MUser follow = userService.getById(followId);

        if (follow == null) {
            return false;
        }

        RelationFollow relationFollow = new RelationFollow();
        relationFollow.setId(UUID.randomUUID().toString());
        relationFollow.setUserId(userId);
        relationFollow.setFllowAvatarurl(follow.getAvatarUrl());
        relationFollow.setFollowNickname(follow.getNickname());
        relationFollow.setFollowId(follow.getId());
        relationFollow.setCreateTime(DateTime.now());
        relationFollow.setStatus(1);

        boolean updateFollowerCount = userService.update(
                new LambdaUpdateWrapper<MUser>()
                        .eq(MUser::getId, follow.getId())
                        .set(MUser::getFollowerCount, follow.getFollowerCount() + 1)
        );

        boolean updateFollowedCount = userService.update(
                new LambdaUpdateWrapper<MUser>()
                        .eq(MUser::getId, userId)
                        .setSql(" followed_count = followed_count + 1 ")
        );

        boolean save = this.save(relationFollow);

        return save&&updateFollowerCount&&updateFollowedCount;

    }

    @Transactional
    @Override
    public boolean unfollow(String userId, String followId) {

        RelationFollow relationFollow = this.getOne(
                new LambdaQueryWrapper<RelationFollow>()
                        .eq(RelationFollow::getUserId, userId)
                        .eq(RelationFollow::getFollowId, followId)
        );

        if (relationFollow==null){
            return false;
        }

        boolean updateFollowerCount = userService.update(
                new LambdaUpdateWrapper<MUser>()
                        .eq(MUser::getId, followId)
                        .setSql(" follower_count = follower_count - 1  ")
        );

        boolean updateFollowedCount = userService.update(
                new LambdaUpdateWrapper<MUser>()
                        .eq(MUser::getId, userId)
                        .setSql(" followed_count = followed_count - 1 ")
        );

        boolean remove = this.removeById(relationFollow.getId());

        return remove&&updateFollowedCount&&updateFollowerCount;

    }
}




