package com.ducksoup.dilivideocontent.mapper;

import com.ducksoup.dilivideocontent.entity.Videoinfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author meichuankutou
* @description 针对表【ct_videoinfo】的数据库操作Mapper
* @createDate 2023-03-08 19:16:37
* @Entity com.ducksoup.dilivideocontent.Entity.Videoinfo
*/
public interface VideoinfoMapper extends BaseMapper<Videoinfo> {

    @Select("SELECT " +
            "info.Id as video_info_id," +
            "info.authorId as video_author_id," +
            "info.collect_count," +
            "info.comment_count," +
            "info.create_time," +
            "info.is_original," +
            "info.is_publish," +
            "info.like_count," +
            "info.open_comment," +
            "info.title," +
            "info.summary," +
            "info.watch_count," +
            "file.Id as video_file_id," +
            "file.fullpath as video_file_url," +
            "file.unique_name as video_file_name," +
            "cover.Id as cover_id, " +
            "cover.unique_name as cover_name," +
            "cover.fullpath as cover_url" +
            " from" +
            " ct_cover as cover," +
            "ct_videofile as file ," +
            "ct_videoinfo as info" +
            " WHERE " +
            "info.Id=#{videoId} " +
            "AND cover.Id = info.cover_id " +
            "AND info.videofile_id = file.Id")
    VideoInfoVo getVideoDetail(@Param("videoId") String videoId);

}




