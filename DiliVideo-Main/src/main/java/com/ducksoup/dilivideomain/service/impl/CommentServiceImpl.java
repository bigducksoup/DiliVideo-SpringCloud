package com.ducksoup.dilivideomain.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideoentity.AuthEntity.MUser;
import com.ducksoup.dilivideomain.Entity.Comment;
import com.ducksoup.dilivideomain.service.CommentService;
import com.ducksoup.dilivideomain.mapper.CommentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author meichuankutou
* @description 针对表【m_comment】的数据库操作Service实现
* @createDate 2023-03-29 22:23:33
*/
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Override
    public String saveComment(String content, MUser user) {

        String id = UUID.randomUUID().toString();

        Comment comment = new Comment();
        comment.setId(id);
        comment.setUserNickname(user.getNickname());
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
}




