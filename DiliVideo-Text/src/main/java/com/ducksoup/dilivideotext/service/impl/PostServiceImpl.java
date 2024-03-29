package com.ducksoup.dilivideotext.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideotext.entity.Post;
import com.ducksoup.dilivideotext.service.PostService;
import com.ducksoup.dilivideotext.mapper.PostMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【post】的数据库操作Service实现
* @createDate 2023-05-04 16:31:23
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{


    @Async
    @Override
    public void increasePostCommentCount(String postId) {
        this.baseMapper.increaseCommentCount(postId);
    }

    @Override
    public List<Post> queryByFollowIds(List<String> followIds, Integer page, Integer pageSize,boolean video_only) {
        Integer start = ( page - 1 ) * pageSize;
        Integer end = pageSize;
        return this.baseMapper.selectByFollowIds(followIds, start, end,video_only);
    }



}




