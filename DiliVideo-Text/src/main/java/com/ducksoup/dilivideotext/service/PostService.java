package com.ducksoup.dilivideotext.service;

import com.ducksoup.dilivideotext.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【post】的数据库操作Service
* @createDate 2023-05-04 16:31:23
*/
public interface PostService extends IService<Post> {

    void increasePostCommentCount(String postId);

    List<Post> queryByFollowIds(List<String> followIds, Integer page, Integer pageSize,boolean video_only);

}
