package com.ducksoup.dilivideocontent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideocontent.entity.Tag;
import com.ducksoup.dilivideocontent.service.TagService;
import com.ducksoup.dilivideocontent.mapper.TagMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author meichuankutou
* @description 针对表【ct_tag】的数据库操作Service实现
* @createDate 2023-10-27 17:10:01
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

    @Override
    public List<Tag> getTagsByVideoInfoId(String videoInfoId) {
        return baseMapper.queryTagsByVideoInfoId(videoInfoId);
    }
}




