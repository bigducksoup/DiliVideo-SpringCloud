package com.ducksoup.dilivideotext.mainservices.action;


import com.ducksoup.dilivideoentity.constant.CONSTANT_MAIN;
import com.ducksoup.dilivideotext.utils.RedisUtil;
import com.ducksoup.dilivideotext.vo.PostVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostLikeService {


    private final RedisUtil redisUtil;

    /**
     * 设置动态like status
     * @param postVos 动态VO
     * @param loginId 用户Id
     */
    public void setPostLikeStatus(List<PostVo> postVos,String loginId){


        //如果未登录设置未false
        if (loginId == null){
            for (PostVo postVo : postVos) {
                postVo.setLiked(false);
            }
            return;
        }

        //用户点赞的post集合（set） 对应的key
        String key = CONSTANT_MAIN.POST_LIKE_PREFIX+loginId;

        List<String> postIds = postVos.stream().map(PostVo::getId).collect(Collectors.toList());

        //key = postId , value = boolean(存在集合中为true，否则为false)
        Map<String, Boolean> map = redisUtil.isSetMember(key, postIds);


        for (PostVo postVo : postVos) {
            postVo.setLiked(map.get(postVo.getId()));
            if (postVo.isLiked()){
                postVo.setLikeCount(postVo.getLikeCount()+1);
            }
        }

    }

}
