package com.ducksoup.dilivideocontent.service;

import com.ducksoup.dilivideocontent.Entity.Videoinfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ducksoup.dilivideoentity.vo.VideoInfoVo;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【ct_videoinfo】的数据库操作Service
* @createDate 2023-03-08 19:16:37
*/
public interface VideoinfoService extends IService<Videoinfo> {

    List<VideoInfoVo> getVideoInfoVoByVideoInfo(List<Videoinfo> videoinfos);

    List<VideoInfoVo> getPublishedVideoById(String userId,int page,int pageSize);

}
