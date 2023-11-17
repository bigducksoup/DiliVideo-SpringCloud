package com.ducksoup.dilivideocontent.mapper;

import com.ducksoup.dilivideocontent.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【ct_tag】的数据库操作Mapper
* @createDate 2023-10-27 17:10:01
* @Entity com.ducksoup.dilivideocontent.entity.Tag
*/
public interface TagMapper extends BaseMapper<Tag> {

    List<Tag> queryTagsByVideoInfoId(@Param("videoInfoId") String videoInfoId);

}




