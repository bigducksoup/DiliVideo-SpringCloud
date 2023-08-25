package com.ducksoup.dilivideomain.mapper;

import com.ducksoup.dilivideomain.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
* @author meichuankutou
* @description 针对表【post】的数据库操作Mapper
* @createDate 2023-05-04 16:31:23
* @Entity com.ducksoup.dilivideomain.Entity.Post
*/
public interface PostMapper extends BaseMapper<Post> {


    @Update("UPDATE post SET comment_count = comment_count + 1 WHERE post.Id = #{postId}")
    boolean increaseCommentCount(@Param("postId") String postId);

}




