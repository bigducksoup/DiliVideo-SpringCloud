package com.ducksoup.dilivideomain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideomain.Entity.Post;
import com.ducksoup.dilivideomain.service.PostService;
import com.ducksoup.dilivideomain.mapper.PostMapper;
import org.springframework.stereotype.Service;

/**
* @author meichuankutou
* @description 针对表【post】的数据库操作Service实现
* @createDate 2023-05-04 16:31:23
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

}




