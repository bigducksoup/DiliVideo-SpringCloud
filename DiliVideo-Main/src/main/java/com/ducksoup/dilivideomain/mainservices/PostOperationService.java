package com.ducksoup.dilivideomain.mainservices;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.ducksoup.dilivideoentity.AuthEntity.MUser;
import com.ducksoup.dilivideoentity.Constant.CONSTANT_MinIO;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideoentity.dto.FileUploadDTO;
import com.ducksoup.dilivideofeign.Auth.AuthServices;
import com.ducksoup.dilivideofeign.Content.ContentServices;
import com.ducksoup.dilivideomain.Controller.Params.TextPostParams;
import com.ducksoup.dilivideomain.Entity.Post;
import com.ducksoup.dilivideomain.Entity.PostImgs;
import com.ducksoup.dilivideomain.Entity.PostModule;
import com.ducksoup.dilivideomain.dto.PostModuleInfo;
import com.ducksoup.dilivideomain.service.PostImgsService;
import com.ducksoup.dilivideomain.service.PostModuleService;
import com.ducksoup.dilivideomain.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.ServiceUnavailableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PostOperationService {


    @Autowired
    private ContentServices contentServices;

    @Autowired
    private PostService postService;

    @Autowired
    private PostImgsService postImgsService;

    @Autowired
    private PostModuleService postModuleService;

    @Autowired
    private AuthServices authServices;


    @Transactional
    public Boolean saveTextPost(TextPostParams postParams) throws Exception {

        String loginId = (String) StpUtil.getLoginId();

        //获取用户信息
        ResponseResult<MUser> userInfo = authServices.getUserInfo(loginId);
        if (userInfo.getCode() != 200) {
            log.error("远程调用失败:main->auth");
            throw new ServiceUnavailableException();
        }

        Map<MultipartFile, String> map = new HashMap<>();

        //上传图片
        List<MultipartFile> files = postParams.getFiles();
        if (files != null) {
            for (MultipartFile f : files) {
                FileUploadDTO fileUploadDTO = new FileUploadDTO();
                fileUploadDTO.setFile(f);
                fileUploadDTO.setBucketName(CONSTANT_MinIO.POST_IMG_BUCKET);
                ResponseResult<String> result = contentServices.uploadFile(fileUploadDTO);
                if (result.getCode() == 200) {
                    map.put(f, result.getData());
                } else {
                    log.error(f.getOriginalFilename() + "上传失败");
                    throw new ServiceUnavailableException();
                }
            }
        }

        //保存模块信息
        PostModuleInfo postModuleInfo = new PostModuleInfo();
        postModuleInfo.setUserInfo(userInfo.getData());
        postModuleInfo.setTypeId("2");
        postModuleInfo.setDesc(postParams.getContent());
        postModuleInfo.setVideoInfoId(null);
        postModuleInfo.setChildPostmoduleId(null);

        String moduleId = this.savePostModule(postModuleInfo);
        //保存图片到数据库
        List<PostImgs> postImgs = new ArrayList<>();
        map.forEach((f, url) -> {
            PostImgs item = new PostImgs();
            item.setId(UUID.randomUUID().toString());
            item.setOriginalName(f.getOriginalFilename());
            item.setUniqueName(UUID.randomUUID().toString());
            item.setPath(url);
            item.setModuleId(moduleId);
            item.setBucket(CONSTANT_MinIO.POST_IMG_BUCKET);
            item.setUploadTime(DateTime.now());
            item.setSize(f.getSize());
            item.setState(1);
            item.setFullpath(url);
            item.setMd5("");
            postImgs.add(item);
        });

        if (!postImgs.isEmpty()){
            postImgsService.saveBatch(postImgs);
        }


        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setModuleId(moduleId);
        post.setTopicId(postParams.getTopicId());
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setShareCount(0);
        post.setCreateTime(DateTime.now());
        post.setStatus(1);

        return postService.save(post);

    }


    /**
     * 保存模块信息
     *
     * @param postModuleInfo 模块信息
     * @return 模块ID
     * @throws Exception 保存异常
     */
    public String savePostModule(PostModuleInfo postModuleInfo) throws Exception {
        MUser userInfo = postModuleInfo.getUserInfo();

        PostModule postModule = new PostModule();
        postModule.setId(UUID.randomUUID().toString());
        postModule.setUserId(userInfo.getId());
        postModule.setUserAvatarUrl(userInfo.getAvatarUrl());
        postModule.setUserNickname(userInfo.getNickname());
        postModule.setDescription(postModuleInfo.getDesc());
        postModule.setTypeId(postModuleInfo.getTypeId());
        postModule.setVideoInfoId(postModuleInfo.getVideoInfoId());
        postModule.setChildPostmoduleId(postModule.getChildPostmoduleId());
        boolean save = postModuleService.save(postModule);

        if (!save) {
            throw new Exception("not save");
        }

        return postModule.getId();

    }


}
