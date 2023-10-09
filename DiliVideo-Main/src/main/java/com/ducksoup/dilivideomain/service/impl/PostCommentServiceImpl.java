package com.ducksoup.dilivideomain.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideoentity.auth.Avatar;
import com.ducksoup.dilivideoentity.auth.MUser;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import com.ducksoup.dilivideomain.entity.PostComment;
import com.ducksoup.dilivideomain.utils.OSSUtils;
import com.ducksoup.dilivideomain.dto.IdMap;
import com.ducksoup.dilivideomain.service.PostCommentService;
import com.ducksoup.dilivideomain.mapper.PostCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【post_comment】的数据库操作Service实现
* @createDate 2023-05-04 16:31:23
*/
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
    implements PostCommentService{


    @Autowired
    private AuthServices authServices;

    @Autowired
    private OSSUtils ossUtils;

    @Override
    public String savePostComment(String content, MUser user) throws Exception {

        ResponseResult<Avatar> responseResult = authServices.getAvatarInfo(user.getAvatarId());
        if (responseResult.getCode()!=200){
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
        postComment.setUserAvatarUrl(ossUtils.makeUrl(avatar.getBucket(),avatar.getPath()));
        postComment.setCreateTime(DateTime.now());
        postComment.setUserId(user.getId());
        postComment.setLikeCount(0);
        postComment.setStatus(1);
        postComment.setUserLevel(user.getLevel());


        boolean save = this.save(postComment);

        if (!save){
            throw new Exception("添加动态评论失败");
        }
        return id;
    }

    @Override
    public List<PostComment> queryPostCommentByPostId(String postId, Integer page, Integer pageSize, boolean orderByTime) {

        Integer start =  ( page - 1 ) * pageSize;
        Integer end = pageSize;

        return this.baseMapper.queryPostCommentByPostId(postId,start,end,orderByTime);
    }

    @Override
    public List<IdMap> queryPostCommentReplies(String commentId, Integer page, Integer pageSize, boolean orderByTime) {
        Integer start =  ( page - 1 ) * pageSize;
        Integer end = pageSize;

        return this.baseMapper.queryCommentReplies(commentId,start,end,orderByTime);
    }
}




