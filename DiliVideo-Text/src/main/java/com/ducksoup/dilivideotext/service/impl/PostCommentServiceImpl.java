package com.ducksoup.dilivideotext.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ducksoup.dilivideoentity.auth.Avatar;
import com.ducksoup.dilivideoentity.auth.MUser;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import com.ducksoup.dilivideotext.entity.PostComment;
import com.ducksoup.dilivideotext.entity.PostCommentReplyComment;
import com.ducksoup.dilivideotext.entity.PostCommentToPost;
import com.ducksoup.dilivideotext.utils.OSSUtils;
import com.ducksoup.dilivideotext.dto.IdMap;
import com.ducksoup.dilivideotext.service.PostCommentService;
import com.ducksoup.dilivideotext.mapper.PostCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author meichuankutou
 * @description 针对表【post_comment】的数据库操作Service实现
 * @createDate 2023-05-04 16:31:23
 */
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
        implements PostCommentService {


    @Autowired
    private AuthServices authServices;

    @Resource
    private PostCommentService postCommentService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OSSUtils ossUtils;

    @Override
    public String savePostComment(String content, MUser user) throws Exception {

        ResponseResult<Avatar> responseResult = authServices.getAvatarInfo(user.getAvatarId());
        if (responseResult.getCode() != 200) {
            log.error("远程调用失败:main->auth:" + responseResult);
            throw new Exception(responseResult.getMsg());
        }

        Avatar avatar = responseResult.getData();

        PostComment postComment = new PostComment();
        String id = UUID.randomUUID().toString();
        postComment.setId(id);
        postComment.setContent(content);
        postComment.setUserNickname(user.getNickname());
        postComment.setUserAvatarPath(avatar.getPath());
        postComment.setUserAvatarBucket(avatar.getBucket());
        postComment.setUserAvatarUrl(ossUtils.makeUrl(avatar.getBucket(), avatar.getPath()));
        postComment.setCreateTime(DateTime.now());
        postComment.setUserId(user.getId());
        postComment.setLikeCount(0);
        postComment.setStatus(1);
        postComment.setUserLevel(user.getLevel());


        boolean save = this.save(postComment);

        if (!save) {
            throw new Exception("添加动态评论失败");
        }
        return id;
    }

    @Override
    public List<PostComment> queryPostCommentByPostId(String postId, Integer page, Integer pageSize, boolean orderByTime) {

        Integer start = (page - 1) * pageSize;
        Integer end = pageSize;

        return this.baseMapper.queryPostCommentByPostId(postId, start, end, orderByTime);
    }

    @Override
    public List<IdMap> queryPostCommentReplies(String commentId, Integer page, Integer pageSize, boolean orderByTime) {
        Integer start = (page - 1) * pageSize;
        Integer end = pageSize;

        return this.baseMapper.queryCommentReplies(commentId, start, end, orderByTime);
    }


    @Override
    public boolean deletePostComment(String commentId) {

        //开启事务 删除相关信息
        transactionTemplate.execute(status -> {
            boolean a = this.removeById(commentId);
            postCommentService.deleteOtherCommentRelatedToPostComment(commentId);
            return a;
        });
        //删除评论
        return true;
    }

    @Override
    public boolean deleteOtherCommentRelatedToPostComment(String commentId) {

        boolean a = Db.remove(Wrappers.lambdaQuery(PostCommentToPost.class).eq(PostCommentToPost::getCommentId, commentId));
        boolean b = Db.remove(Wrappers.lambdaQuery(PostCommentReplyComment.class)
                .eq(PostCommentReplyComment::getCommentId, commentId)
                .or()
                .eq(PostCommentReplyComment::getReplyCommentId, commentId)
                .or().
                eq(PostCommentReplyComment::getFatherCommentId, commentId));

        return a&&b;
    }


}




