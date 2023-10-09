package com.ducksoup.dilivideomain.mainservices;


import cn.dev33.satoken.stp.StpUtil;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MAIN;
import com.ducksoup.dilivideomain.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public boolean dispatchAction(Integer targetType, String targetId) {

        boolean res;

        LikeActionHandler handler = new LikeActionHandler();
        handler.setTargetId(targetId);
        handler.setRedisUtil(redisUtil);
        switch (targetType) {
            case POST:
                handler.setPREFIX(CONSTANT_MAIN.POSTLIKEPERFIX);
                handler.setREDISLOCK(CONSTANT_MAIN.POSTLIKEUPDATELOCK);
                res = handler.doHandel((id, count) -> postOperationService.updatePostLikeCount(id, count));
                break;
            case POSTCOMMENT:
                handler.setPREFIX(CONSTANT_MAIN.POSTCOMMENTLIKEPERFIX);
                handler.setREDISLOCK(CONSTANT_MAIN.POSTCOMMENTUPDATELOCK);
                res = handler.doHandel((id, count) -> postCommentLikeService.updatePostCommentLikeCount(id, count));
                break;
            case COMMENT:
                handler.setPREFIX(CONSTANT_MAIN.COMMENTLIKEPERFIX);
                handler.setREDISLOCK(CONSTANT_MAIN.COMMENTUPDATELOCK);
                res = handler.doHandel((id, count) -> commentLikeService.updateCommentLikeCount(id, count));
                break;
            default:
                res = false;
                break;
        }

        return res;
    }



    public boolean checkLike(Integer targetType, String targetId) {

        String key;

        switch (targetType){
            case POST:
                key = CONSTANT_MAIN.POSTLIKEPERFIX+targetId;
                break;
            case POSTCOMMENT:
                key = CONSTANT_MAIN.POSTCOMMENTLIKEPERFIX+targetId;
                break;
            case COMMENT:
                key = CONSTANT_MAIN.COMMENTLIKEPERFIX+targetId;
                break;
            default:
                return false;
        }

        return redisUtil.checkExistSetItem(key, StpUtil.getLoginId());

    }


}
