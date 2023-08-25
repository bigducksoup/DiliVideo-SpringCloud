package com.ducksoup.dilivideomain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ducksoup.dilivideomain.entity.PostType;
import com.ducksoup.dilivideomain.service.PostTypeService;
import com.ducksoup.dilivideomain.mapper.PostTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author meichuankutou
* @description 针对表【post_type】的数据库操作Service实现
* @createDate 2023-05-04 16:31:23
*/
@Service
public class PostTypeServiceImpl extends ServiceImpl<PostTypeMapper, PostType>
    implements PostTypeService{

}




