package com.ducksoup.dilivideomain.mainservices;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.ducksoup.dilivideomain.Entity.Post;
import com.ducksoup.dilivideomain.Entity.PostImgs;
import com.ducksoup.dilivideomain.Entity.PostModule;
import com.ducksoup.dilivideomain.service.PostImgsService;
import com.ducksoup.dilivideomain.service.PostModuleService;
import com.ducksoup.dilivideomain.service.PostService;

import com.ducksoup.dilivideomain.vo.ModuleVo;
import com.ducksoup.dilivideomain.vo.PostVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PostQueryService {

    @Autowired
    private PostService postService;

    @Autowired
    private PostModuleService postModuleService;

    @Autowired
    private PostImgsService postImgsService;


    public List<PostVo> getPostByUserId(String userId,Integer page){
        //分页
        Page<PostModule> pager = new Page<>(page,20);

        LambdaQueryWrapper<PostModule> postQuery = new LambdaQueryWrapper<PostModule>()
                .eq(PostModule::getUserId, userId);

        //查询module
        Page<PostModule> queryRes = postModuleService.page(pager,postQuery);

        List<PostModule> modules = queryRes.getRecords();
        //获取moduleID集合
        List<String> moduleIds = modules.stream().map(PostModule::getId).collect(Collectors.toList());

        //根据moduleID获取Post
        List<Post> postList = postService.list(new LambdaQueryWrapper<Post>().in(Post::getModuleId, moduleIds));

        //根据moduleID获取Imgs
        List<PostImgs> postImgs = postImgsService.list(new LambdaQueryWrapper<PostImgs>().in(PostImgs::getModuleId, moduleIds));

        //key:moduleId,value:imgList
        Map<String, List<String>> moduleIdImgList = postImgs.stream().collect(Collectors.groupingBy(PostImgs::getModuleId, Collectors.mapping(PostImgs::getFullpath, Collectors.toList())));


        Map<String, PostModule> moduleMap = modules.stream().collect(Collectors.toMap(PostModule::getId, item -> item));

        List<PostVo> postVos = new ArrayList<>();


        //把post放入postVo
        postList.forEach(item->{
            PostVo postVo = new PostVo();
            BeanUtil.copyProperties(item,postVo);
            PostModule postModule = moduleMap.get(item.getModuleId());
            ModuleVo moduleVo = new ModuleVo();
            BeanUtil.copyProperties(postModule,moduleVo);
            moduleVo.setImgs(moduleIdImgList.get(item.getModuleId()));
            postVo.setModule(moduleVo);
            postVos.add(postVo);
        });

        return postVos;
    }



}
