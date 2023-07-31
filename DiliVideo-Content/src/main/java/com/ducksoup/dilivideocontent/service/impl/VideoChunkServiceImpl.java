package com.ducksoup.dilivideocontent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideocontent.entity.VideoChunk;
import com.ducksoup.dilivideocontent.service.VideoChunkService;
import com.ducksoup.dilivideocontent.mapper.VideoChunkMapper;
import org.springframework.stereotype.Service;

/**
* @author meichuankutou
* @description 针对表【ct_video_chunk】的数据库操作Service实现
* @createDate 2023-07-29 23:55:25
*/
@Service
public class VideoChunkServiceImpl extends ServiceImpl<VideoChunkMapper, VideoChunk>
    implements VideoChunkService{

    @Override
    public VideoChunk checkExist(String md5) {
        return this.getOne(new LambdaQueryWrapper<VideoChunk>().eq(VideoChunk::getMd5, md5));
    }
}




