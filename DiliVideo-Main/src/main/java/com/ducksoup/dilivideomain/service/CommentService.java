package com.ducksoup.dilivideomain.service;

import com.ducksoup.dilivideoentity.authEntity.MUser;
import com.ducksoup.dilivideomain.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【m_comment】的数据库操作Service
* @createDate 2023-03-29 22:23:33
*/
public interface CommentService extends IService<Comment> {

    String saveComment(String content, MUser user) throws Exception;

    List<String> queryCommentIdsByVideoInfoIdSortByLikeCount(String videoInfoId, Integer page, Integer pageSize);

    List<String> queryCommentIdsByVideoInfoIdSortByTime(String videoInfoId,Integer page,Integer pageSize);

}
