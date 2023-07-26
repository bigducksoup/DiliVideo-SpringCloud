package com.ducksoup.dilivideomain.mapper;

import com.ducksoup.dilivideomain.Entity.PostComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ducksoup.dilivideomain.dto.IdMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【post_comment】的数据库操作Mapper
* @createDate 2023-05-04 16:31:23
* @Entity com.ducksoup.dilivideomain.Entity.PostComment
*/
public interface PostCommentMapper extends BaseMapper<PostComment> {


    List<PostComment> queryPostCommentByPostId(@Param("postId") String postId,@Param("start") Integer start,@Param("end") Integer end ,@Param("orderByTime") boolean orderByTime);

    List<IdMap> queryCommentReplies(@Param("commentId") String commentId, @Param("start") Integer start, @Param("end") Integer end, @Param("orderByTime") boolean orderByTime);

}




