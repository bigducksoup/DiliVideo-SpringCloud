package com.ducksoup.dilivideomain.mapper;

import com.ducksoup.dilivideomain.Entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【m_comment】的数据库操作Mapper
* @createDate 2023-03-29 22:23:33
* @Entity com.ducksoup.dilivideomain.Entity.Comment
*/
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT " +
            "m_comment.Id" +
            " from " +
            "m_comment_videoinfo,m_comment" +
            " WHERE " +
            " m_comment_videoinfo.videoinfo_id = #{videoInfoId}" +
            " and " +
            "m_comment_videoinfo.comment_id = m_comment.Id" +
            " ORDER BY " +
            "m_comment.like_count" +
            " DESC " +
            " LIMIT " +
            "#{limitStart},#{limitEnd}"

    )
    List<String> queryCommentIdsByVideoInfoIdOrderByLikeCount(
            @Param("videoInfoId") String videoInfoId,
            @Param("limitStart") Integer limitStart,
            @Param("limitEnd") Integer limitEnd);



    @Select(" SELECT " +
            "m_comment.Id" +
            " from " +
            "m_comment_videoinfo,m_comment" +
            " WHERE " +
            "m_comment_videoinfo.videoinfo_id = #{videoInfoId}" +
            " and " +
            "m_comment_videoinfo.comment_id = m_comment.Id" +
            " ORDER BY " +
            "m_comment.create_time" +
            " DESC " +
            " LIMIT " +
            "#{limitStart},#{limitEnd}"
    )
    List<String> queryCommentIdsByVideoInfoIdOrderByTime(
            @Param("videoInfoId") String videoInfoId,
            @Param("limitStart") Integer limitStart,
            @Param("limitEnd") Integer limitEnd);



}




