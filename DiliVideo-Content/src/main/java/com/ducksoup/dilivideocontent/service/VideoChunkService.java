package com.ducksoup.dilivideocontent.service;

import com.ducksoup.dilivideocontent.entity.VideoChunk;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author meichuankutou
* @description 针对表【ct_video_chunk】的数据库操作Service
* @createDate 2023-07-29 23:55:25
*/
public interface VideoChunkService extends IService<VideoChunk> {

    VideoChunk checkExist(String md5);

}
