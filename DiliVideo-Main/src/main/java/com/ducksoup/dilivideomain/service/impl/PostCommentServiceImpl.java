package com.ducksoup.dilivideomain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideomain.Entity.PostComment;
import com.ducksoup.dilivideomain.service.PostCommentService;
import com.ducksoup.dilivideomain.mapper.PostCommentMapper;
import org.springframework.stereotype.Service;

/**
* @author meichuankutou
* @description 针对表【post_comment】的数据库操作Service实现
* @createDate 2023-05-04 16:31:23
*/
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
    implements PostCommentService{

}




