package com.ducksoup.dilivideomain.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ducksoup.dilivideoentity.auth.Avatar;
import com.ducksoup.dilivideoentity.auth.MUser;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import com.ducksoup.dilivideomain.entity.Comment;
import com.ducksoup.dilivideomain.entity.CommentReplyComment;
import com.ducksoup.dilivideomain.entity.CommentVideoinfo;
import com.ducksoup.dilivideomain.service.CommentService;
import com.ducksoup.dilivideomain.mapper.CommentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【m_comment】的数据库操作Service实现
* @createDate 2023-03-29 22:23:33
*/
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{


    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private AuthServices authServices;

    @Autowired
    private CommentService commentService;


    @Override
    public String saveComment(String content, MUser user) throws Exception {

        ResponseResult<Avatar> responseResult = authServices.getAvatarInfo(user.getAvatarId());
        if (responseResult.getCode()!=200){
            log.error("远程调用失败:main->auth:" + responseResult);
            throw new Exception(responseResult.getMsg());
        }

        Avatar avatar = responseResult.getData();

        String id = UUID.randomUUID().toString();

        Comment comment = new Comment();
        comment.setId(id);
        comment.setUserNickname(user.getNickname());

        comment.setUserAvatarBucket(avatar.getBucket());
        comment.setUserAvatarPath(avatar.getPath());

        comment.setUserAvatarUrl(user.getAvatarUrl());
        comment.setCreateTime(DateTime.now());
        comment.setUserId(user.getId());
        comment.setLikeCount(0);
        comment.setStatus(1);
        comment.setUserLevel(user.getLevel());
        comment.setContent(content);


        boolean save = this.save(comment);

        if (save){
            log.info("评论添加成功");
        }

        return save? id:null;
    }


    public List<String> queryCommentIdsByVideoInfoIdSortByLikeCount(String videoInfoId, Integer page, Integer pageSize){
        Integer limitStart = (page-1)*pageSize;
        Integer limitEnd =  pageSize;
        List<String> ids = commentMapper.queryCommentIdsByVideoInfoIdOrderByLikeCount(videoInfoId, limitStart, limitEnd);
        return ids;
    }


    public List<String> queryCommentIdsByVideoInfoIdSortByTime(String videoInfoId,Integer page,Integer pageSize){
        Integer limitStart = (page-1)*pageSize;
        Integer limitEnd = pageSize;
        List<String> ids = commentMapper.queryCommentIdsByVideoInfoIdOrderByTime(videoInfoId, limitStart, limitEnd);
        return ids;
    }

    @Override
    public boolean deleteVideoComment(String commentId) {
        boolean commentDeleted = commentService.deleteOtherCommentRelatedToVideoComment(commentId);

        return commentDeleted;
    }


    @Transactional
    @Override
    public boolean deleteOtherCommentRelatedToVideoComment(String commentId){

        boolean a = Db.removeById(commentId, Comment.class);
        boolean b = Db.remove(Wrappers.lambdaQuery(CommentVideoinfo.class).eq(CommentVideoinfo::getCommentId, commentId));
        boolean c = Db.remove(Wrappers.lambdaQuery(CommentReplyComment.class).eq(CommentReplyComment::getFatherCommentId, commentId).or().eq(CommentReplyComment::getCommentId, commentId).or().eq(CommentReplyComment::getReplyCommentId, commentId));

        return true;

    }


}




