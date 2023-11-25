package com.ducksoup.dilivideotext.mainservices.action;


import cn.dev33.satoken.stp.StpUtil;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MAIN;
import com.ducksoup.dilivideotext.mainservices.LikeActionHandler;
import com.ducksoup.dilivideotext.mainservices.PostOperationService;
import com.ducksoup.dilivideotext.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 处理用户动作
 */
@Service
public class ActionService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PostCommentLikeService postCommentLikeService;

    @Autowired
    private CommentLikeService commentLikeService;


    @Autowired
    private PostOperationService postOperationService;

    final private static int POST = 0;

    final private static int POSTCOMMENT = 1;

    final private static int COMMENT = 2;


    /**
     * 执行点赞动作
     * @param targetType 目标类型 动态 = 0 ； 动态评论 = 1 ； 视频评论 = 2
     * @param targetId 目标id
     * @return 点赞状态
     */
    public boolean dispatchAction(Integer targetType, String targetId) {

        boolean res;
        //初始化handler，设置对应信息
        LikeActionHandler handler = new LikeActionHandler();
        handler.setTargetId(targetId);
        handler.setRedisUtil(redisUtil);
        handler.setUserId((String) StpUtil.getLoginId());

        //根据目标类型执行动作
        switch (targetType) {
            case POST:
                handler.setLIKE_COUNT_PREFIX(CONSTANT_MAIN.POST_LIKE_COUNT_PREFIX);
                handler.setPREFIX(CONSTANT_MAIN.POST_LIKE_PREFIX);
                handler.setREDISLOCK(CONSTANT_MAIN.POST_LIKE_UPDATE_LOCK);
                res = handler.doHandel((id, count) -> postOperationService.updatePostLikeCount(id, count));
                break;
            case POSTCOMMENT:
                handler.setLIKE_COUNT_PREFIX(CONSTANT_MAIN.POST_COMMENT_LIKE_COUNT_PREFIX);
                handler.setPREFIX(CONSTANT_MAIN.POST_COMMENT_LIKE_PREFIX);
                handler.setREDISLOCK(CONSTANT_MAIN.POST_COMMENT_UPDATE_LOCK);
                res = handler.doHandel((id, count) -> postCommentLikeService.updatePostCommentLikeCount(id, count));
                break;
            case COMMENT:
                handler.setLIKE_COUNT_PREFIX(CONSTANT_MAIN.COMMENT_LIKE_COUNT_PREFIX);
                handler.setPREFIX(CONSTANT_MAIN.COMMENT_LIKE_PREFIX);
                handler.setREDISLOCK(CONSTANT_MAIN.COMMENT_UPDATE_LOCK);
                res = handler.doHandel((id, count) -> commentLikeService.updateCommentLikeCount(id, count));
                break;
            default:
                res = false;
                break;
        }

        return res;
    }



    public boolean checkLike(Integer targetType, String targetId,String userId) {

        String key;

        switch (targetType){
            case POST:
                key = CONSTANT_MAIN.POST_LIKE_PREFIX+userId;
                break;
            case POSTCOMMENT:
                key = CONSTANT_MAIN.POST_COMMENT_LIKE_PREFIX+userId;
                break;
            case COMMENT:
                key = CONSTANT_MAIN.COMMENT_LIKE_PREFIX+userId;
                break;
            default:
                return false;
        }

        return redisUtil.checkExistSetItem(key,targetId);

    }


}
