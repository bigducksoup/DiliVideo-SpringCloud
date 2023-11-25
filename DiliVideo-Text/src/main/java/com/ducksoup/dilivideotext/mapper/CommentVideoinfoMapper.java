package com.ducksoup.dilivideotext.mapper;

import com.ducksoup.dilivideotext.entity.Comment;
import com.ducksoup.dilivideotext.entity.CommentVideoinfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【m_comment_videoinfo】的数据库操作Mapper
* @createDate 2023-03-29 22:25:09
* @Entity com.ducksoup.dilivideomain.Entity.CommentVideoinfo
*/
public interface CommentVideoinfoMapper extends BaseMapper<CommentVideoinfo> {





    List<Comment> queryCommentByVideoInfoId(@Param("videoInfoId") String videoInfoId,@Param("mode") int mode,@Param("limit01") int limit01,@Param("limit02") int limit02);


}




