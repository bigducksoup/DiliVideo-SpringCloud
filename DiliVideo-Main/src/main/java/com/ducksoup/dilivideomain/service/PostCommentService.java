package com.ducksoup.dilivideomain.service;

import com.ducksoup.dilivideoentity.AuthEntity.MUser;
import com.ducksoup.dilivideomain.Entity.PostComment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ducksoup.dilivideomain.dto.IdMap;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【post_comment】的数据库操作Service
* @createDate 2023-05-04 16:31:23
*/
public interface PostCommentService extends IService<PostComment> {


    String savePostComment(String content, MUser user) throws Exception;


    List<PostComment> queryPostCommentByPostId(String postId,Integer page,Integer pageSize,boolean orderByTime);

    List<IdMap> queryPostCommentReplies(String commentId, Integer page, Integer pageSize, boolean orderByTime);

}
