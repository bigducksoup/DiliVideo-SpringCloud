package com.ducksoup.dilivideotext.mainservices.action;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MAIN;
import com.ducksoup.dilivideotext.entity.Comment;
import com.ducksoup.dilivideotext.service.CommentService;
import com.ducksoup.dilivideotext.utils.RedisUtil;
import com.ducksoup.dilivideotext.vo.CommentItemVo;
import com.ducksoup.dilivideotext.vo.ReplyVo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class CommentLikeService {


    @Resource
    private CommentService commentService;

    @Resource
    private RedisUtil redisUtil;



    @Async(value = "ThreadPool")
    public void updateCommentLikeCount(String commentId,Integer count){

        commentService.update(new LambdaUpdateWrapper<Comment>().eq(Comment::getId,commentId).set(Comment::getLikeCount,count));

    }



    //TODO 是否实时查询点赞数
    public void setLikeCount(List<CommentItemVo> commentItemVos){

        //获取评论点赞数在缓存中对应的key
        List<String> keys = commentItemVos.stream().map(item -> CONSTANT_MAIN.COMMENT_LIKE_COUNT_PREFIX + item.getId()).collect(Collectors.toList());

        List<Object> values = redisUtil.mget(keys);

        for (int i = 0; i < commentItemVos.size(); i++) {
            Object value = values.get(i);
            if (value!=null){
                commentItemVos.get(i).setLikeCount((Integer) value);
            }
        }
    }


    //TODO test
    /**
     * 设置是否点赞
     * @param commentItemVos 评论VO（视频评论）
     * <p>
     *      redis中的结构
     *      key = CONSTANT_MAIN.COMMENT_LIKE_PREFIX+loginId
     *      value = Set (commentIds)
     * </p>
     */
    public void setLikeStatus(List<CommentItemVo> commentItemVos,String loginId){

        //如果没有登陆设置为false
        if (loginId==null){
            for (CommentItemVo commentItemVo : commentItemVos) {
                commentItemVo.setLiked(false);
            }
            return;
        }

        //查询评论的id
        List<String> commentIds = commentItemVos.stream().map(CommentItemVo::getId).collect(Collectors.toList());

        //查询是否有点赞记录
        Map<String, Boolean> map = redisUtil.isSetMember(CONSTANT_MAIN.COMMENT_LIKE_PREFIX + loginId, commentIds);

        //设置点赞记录
        for (CommentItemVo commentItemVo : commentItemVos) {
            commentItemVo.setLiked(map.get(commentItemVo.getId()));
            if (commentItemVo.isLiked()){
                commentItemVo.setLikeCount(commentItemVo.getLikeCount()+1);
            }
        }

    }


    /**
     * 同上只是参数不同，对应数据库中均为Comment表
     * @param replyVos （视频评论）回复的VO
     * @param loginId   用户Id
     * <p>
     *      redis中的结构
     *      key = CONSTANT_MAIN.COMMENT_LIKE_PREFIX+loginId
     *      value = Set (commentIds)
     * </p>
     */
    public void setReplyLikeStatus(List<ReplyVo> replyVos, String loginId){

        //如果没有登陆设置为false
        if (loginId==null){
            for (ReplyVo replyVo : replyVos) {
                replyVo.setLiked(false);
            }
            return;
        }

        //查询评论的id
        List<String> commentIds = replyVos.stream().map(ReplyVo::getId).collect(Collectors.toList());

        //查询是否有点赞记录
        Map<String, Boolean> map = redisUtil.isSetMember(CONSTANT_MAIN.COMMENT_LIKE_PREFIX + loginId, commentIds);

        //设置点赞记录
        for (ReplyVo replyVo : replyVos) {
            replyVo.setLiked(map.get(replyVo.getId()));
            if (replyVo.isLiked()){
                replyVo.setLikeCount(replyVo.getLikeCount()+1);
            }
        }


    }


}
