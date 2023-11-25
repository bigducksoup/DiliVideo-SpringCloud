package com.ducksoup.dilivideotext.mainservices.action;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MAIN;
import com.ducksoup.dilivideotext.entity.PostComment;
import com.ducksoup.dilivideotext.service.PostCommentService;
import com.ducksoup.dilivideotext.utils.RedisUtil;
import com.ducksoup.dilivideotext.vo.PostCommentVo;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class PostCommentLikeService {



    private final PostCommentService postCommentService;


    private final RedisUtil redisUtil;


    @Async(value = "ThreadPool")
    public void updatePostCommentLikeCount(String postCommentId,Integer count){

        postCommentService.update(new LambdaUpdateWrapper<PostComment>().eq(PostComment::getId,postCommentId).set(PostComment::getLikeCount,count));

    }


    /**
     *
     * @param postCommentVos 动态评论VO
     * @param loginId 用户idID
     */
    public void setPostCommentLikeStatus(List<PostCommentVo> postCommentVos,String loginId){


        //未登陆设置为false
        if (loginId == null){
            for (PostCommentVo postCommentVo : postCommentVos) {
                postCommentVo.setLiked(false);
            }
            return;
        }

        //存储用户点赞的动态评论对应的key
        String key = CONSTANT_MAIN.POST_COMMENT_LIKE_PREFIX+loginId;


        List<String> postCommentIds = postCommentVos.stream().map(PostCommentVo::getId).collect(Collectors.toList());

        Map<String, Boolean> map = redisUtil.isSetMember(key, postCommentIds);


        for (PostCommentVo postCommentVo : postCommentVos) {
            postCommentVo.setLiked(map.get(postCommentVo.getId()));
            if (postCommentVo.isLiked()){
                postCommentVo.setLikeCount(postCommentVo.getLikeCount()+1);
            }
        }
    }







}
