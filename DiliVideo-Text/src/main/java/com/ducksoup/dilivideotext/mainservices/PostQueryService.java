package com.ducksoup.dilivideotext.mainservices;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.ducksoup.dilivideoentity.vo.UserVo;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import com.ducksoup.dilivideotext.aop.annonation.PerformanceLog;
import com.ducksoup.dilivideotext.entity.Post;
import com.ducksoup.dilivideotext.entity.PostImgs;
import com.ducksoup.dilivideotext.entity.PostModule;
import com.ducksoup.dilivideotext.mainservices.action.PostLikeService;
import com.ducksoup.dilivideotext.utils.OSSUtils;
import com.ducksoup.dilivideotext.service.PostImgsService;
import com.ducksoup.dilivideotext.service.PostModuleService;
import com.ducksoup.dilivideotext.service.PostService;

import com.ducksoup.dilivideotext.vo.ModuleVo;
import com.ducksoup.dilivideotext.vo.PostVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    private AuthServices authServices;

    @Autowired
    private PostModuleService postModuleService;

    @Autowired
    private PostImgsService postImgsService;

    @Autowired
    private PostQueryService postQueryService;

    @Autowired
    private OSSUtils ossUtils;

    @Resource
    private PostLikeService postLikeService;


    public List<PostVo> getPostByUserId(String userId,Integer page,boolean video_only){
        //分页
        Page<PostModule> pager = new Page<>(page,20);

        LambdaQueryWrapper<PostModule> postQuery = new LambdaQueryWrapper<PostModule>()
                .eq(PostModule::getUserId, userId).eq(video_only,PostModule::getTypeId,1);

        //查询module
        Page<PostModule> queryRes = postModuleService.page(pager,postQuery);

        List<PostModule> modules = queryRes.getRecords();

        if (modules.isEmpty()){
            return new ArrayList<>();
        }

        //获取moduleID集合
        List<String> moduleIds = modules.stream().map(PostModule::getId).collect(Collectors.toList());

        //根据moduleID获取Post
        List<Post> postList = postService.list(new LambdaQueryWrapper<Post>().in(Post::getModuleId, moduleIds).orderByDesc(Post::getCreateTime));


        return getPostVOByPosts(postList);
    }


    public long getUserPostCount(String userId){

        return  postModuleService.count(new LambdaQueryWrapper<PostModule>().eq(PostModule::getUserId,userId));

    }

    /**
     * 根据PostId查询PostVo
     * @param postId postId
     * @return PostVo
     */
    public PostVo queryPostById(String postId){

        Post post = postService.getById(postId);

        if (post==null)return null;

        ModuleVo moduleVo = queryModuleById(post.getModuleId());

        PostVo postVo = new PostVo();
        BeanUtil.copyProperties(post,postVo);
        postVo.setModuleVO(moduleVo);

        return postVo;
    }


    public ModuleVo queryModuleById(String moduleId){
        PostModule postModule = postModuleService.getById(moduleId);
        List<String> imgUrls = queryPostImgByModuleId(moduleId);
        ModuleVo moduleVo = new ModuleVo();
        BeanUtil.copyProperties(postModule,moduleVo);
        moduleVo.setImgs(imgUrls);
        moduleVo.setUserAvatarUrl(ossUtils.makeUrl(postModule.getUserAvatarBucket(),postModule.getUserAvatarPath()));

        return moduleVo;
    }


    public List<String> queryPostImgByModuleId(String moduleId){
        List<PostImgs> postImgs = postImgsService.list(new LambdaQueryWrapper<PostImgs>().eq(PostImgs::getModuleId,moduleId));

        List<String> res = new ArrayList<>();

        postImgs.forEach(item->{
            res.add(ossUtils.makeUrl(item.getBucket(),item.getPath()));
        });
        return res;

    }


    public List<PostVo> queryFollowPost(String userId,Integer page,Integer pageSize,boolean video_only){

        List<UserVo> followList = authServices.getFollowList(userId);

        List<String> followIds = followList.stream().map(UserVo::getId).collect(Collectors.toList());
        if (followIds.isEmpty())return new ArrayList<>();

        //查询关注用户的动态
        List<Post> posts = postService.queryByFollowIds(followIds, page, pageSize,video_only);


        return postQueryService.getPostVOByPosts(posts);


    }


    @PerformanceLog
    public List<PostVo> getPostVOByPosts(List<Post> posts){

        List<String> moduleIds = posts.stream().map(Post::getModuleId).collect(Collectors.toList());

        List<PostVo> res = new ArrayList<>();

        if (moduleIds.isEmpty()){
            return res;
        }

        List<PostModule> modules = postModuleService.list(new LambdaQueryWrapper<PostModule>()
                .in(PostModule::getId, moduleIds));


        List<ModuleVo> moduleVos = postQueryService.getModuleVOByModules(modules);

        Map<String, ModuleVo> moduleId_moduleVo = moduleVos.stream().collect(Collectors.toMap(ModuleVo::getId, item -> item));



        posts.forEach(item->{
            PostVo postVo = new PostVo();
            BeanUtil.copyProperties(item,postVo);
            ModuleVo moduleVo = moduleId_moduleVo.get(item.getModuleId());
            postVo.setModuleVO(moduleVo);
            res.add(postVo);
        });

        //设置点赞状态
        postLikeService.setPostLikeStatus(res, StpUtil.isLogin()? (String) StpUtil.getLoginId() :null);

        return res;
    }


    @PerformanceLog
    public List<ModuleVo> getModuleVOByModules(List<PostModule> modules){

        List<PostImgs> postImgs = postImgsService.list(new LambdaQueryWrapper<PostImgs>().in(PostImgs::getModuleId, modules.stream().map(PostModule::getId).collect(Collectors.toList())));

        //根据配置里的minio地址转换full_path
        postImgs.forEach(item->{
            item.setFullpath(ossUtils.makeUrl(item.getBucket(),item.getPath()));
        });

        Map<String, List<String>> moduleId_ImgUrls = postImgs.stream().collect((Collectors.groupingBy(PostImgs::getModuleId, Collectors.mapping(PostImgs::getFullpath, Collectors.toList()))));

        List<ModuleVo> res = new ArrayList<>();

        modules.forEach(item->{
            ModuleVo moduleVo = new ModuleVo();
            BeanUtil.copyProperties(item,moduleVo);
            List<String> imgUrls = moduleId_ImgUrls.get(item.getId());
            moduleVo.setUserAvatarUrl(ossUtils.makeUrl(item.getUserAvatarBucket(),item.getUserAvatarPath()));
            if (imgUrls==null){
                moduleVo.setImgs(new ArrayList<>());
            }else {
                moduleVo.setImgs(imgUrls);
            }
            res.add(moduleVo);
        });

        return res;
    }


}
