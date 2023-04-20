package com.ducksoup.dilivideomain.service;

import com.ducksoup.dilivideoentity.AuthEntity.MUser;
import com.ducksoup.dilivideomain.Entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author meichuankutou
* @description 针对表【m_comment】的数据库操作Service
* @createDate 2023-03-29 22:23:33
*/
public interface CommentService extends IService<Comment> {

    String saveComment(String content, MUser user);

}
