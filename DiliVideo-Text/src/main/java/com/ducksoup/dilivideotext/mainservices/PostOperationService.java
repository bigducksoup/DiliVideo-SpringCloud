package com.ducksoup.dilivideotext.mainservices;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideoentity.auth.Avatar;
import com.ducksoup.dilivideoentity.auth.MUser;
import com.ducksoup.dilivideoentity.content.Videoinfo;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideoentity.dto.FileInfo;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import com.ducksoup.dilivideofeign.content.ContentServices;
import com.ducksoup.dilivideotext.controller.params.ForwardTextPostParams;
import com.ducksoup.dilivideotext.controller.params.ForwardVideoParams;
import com.ducksoup.dilivideotext.controller.params.TextPostParams;
import com.ducksoup.dilivideotext.entity.*;
import com.ducksoup.dilivideotext.dto.PostModuleInfo;
import com.ducksoup.dilivideotext.mq.consumer.VideoPostAddConsumer;
import com.ducksoup.dilivideotext.service.*;
import com.ducksoup.dilivideotext.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.ServiceUnavailableException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class PostOperationService {


    @Autowired
    private ContentServices contentServices;

    @Autowired
    private PostService postService;


    @Autowired
    private PostModuleService postModuleService;

    @Autowired
    private AuthServices authServices;


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PostAssetsService postAssetsService;

    @Autowired
    private PostCommentService postCommentService;

    @Autowired
    private PostCommentToPostService commentToPostService;

    @Autowired
    private PostCommentReplyCommentService commentReplyCommentService;


    /**
     * 发布文字动态（可配图）
     * @param postParams TextPostParams
     * @see TextPostParams
     * @return Boolean
     * @throws Exception 远程调用和上传文件
     */
    @Transactional
    public Boolean saveTextPost(TextPostParams postParams) throws Exception {

        String loginId = (String) StpUtil.getLoginId();

        //获取用户信息
        ResponseResult<MUser> userInfo = authServices.getUserInfo(loginId);
        if (userInfo.getCode() != 200) {
            log.error("远程调用失败:main->auth");
            throw new ServiceUnavailableException();
        }


        //上传图片
        List<MultipartFile> files = postParams.getFiles();
        Map<MultipartFile, FileInfo> map = postAssetsService.uploadFiles(files);

        //保存模块信息
        PostModuleInfo postModuleInfo = new PostModuleInfo();
        postModuleInfo.setUserInfo(userInfo.getData());
        postModuleInfo.setTypeId("2");
        postModuleInfo.setDesc(postParams.getContent());
        postModuleInfo.setVideoInfoId(null);
        postModuleInfo.setChildPostId(null);

        String moduleId = this.savePostModule(postModuleInfo);
        //保存图片到数据库

        postAssetsService.savePostImg(map,moduleId);


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
     * 发布视频自动发布动态（消费MQ中的消息）
     * @see VideoPostAddConsumer
     * @param videoInfoId 视频信息id
     * @return Boolean
     * @throws Exception ServiceUnavailableException
     */
    @Transactional
    public Boolean saveVideoPublishPost(String videoInfoId) throws Exception {
        //获取视频信息
        Videoinfo videoinfo = contentServices.getVideoInfoById(videoInfoId).getData();

        //作者信息
        MUser user = authServices.getUserInfo(videoinfo.getAuthorid()).getData();



        PostModuleInfo postModuleInfo = new PostModuleInfo();
        postModuleInfo.setUserInfo(user);
        postModuleInfo.setTypeId("1");
        postModuleInfo.setDesc("");
        postModuleInfo.setVideoInfoId(videoInfoId);
        postModuleInfo.setChildPostId(null);

        String moduleId = this.savePostModule(postModuleInfo);

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setModuleId(moduleId);
        post.setTopicId("3");
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setShareCount(0);
        post.setCreateTime(DateTime.now());
        post.setStatus(1);

        return postService.save(post);
    }


    /**
     * 转发文字动态（可配图）
     * @param params ForwardTextPostParams
     * @see ForwardTextPostParams
     * @return Boolean
     * @throws Exception ServiceUnavailableException
     */
    @Transactional
    public Boolean saveForwardTextPost(ForwardTextPostParams params) throws Exception {
        String loginId = (String) StpUtil.getLoginId();
        //获取用户信息
        ResponseResult<MUser> userInfo = authServices.getUserInfo(loginId);
        if (userInfo.getCode() != 200) {
            log.error("远程调用失败:main->auth");
            throw new ServiceUnavailableException();
        }

        Map<MultipartFile, FileInfo> fileInfoMap = postAssetsService.uploadFiles(params.getFiles());

        PostModuleInfo postModuleInfo = new PostModuleInfo();
        postModuleInfo.setUserInfo(userInfo.getData());
        postModuleInfo.setTypeId("4");
        postModuleInfo.setDesc(params.getContent());
        postModuleInfo.setVideoInfoId(null);
        postModuleInfo.setChildPostId(params.getPostId());

        String moduleId = this.savePostModule(postModuleInfo);


        postAssetsService.savePostImg(fileInfoMap,moduleId);


        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setModuleId(moduleId);
        post.setTopicId("-1");
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setShareCount(0);
        post.setCreateTime(DateTime.now());
        post.setStatus(1);

        return postService.save(post);

    }


    @Transactional
    public boolean saveForwardVideoPost(ForwardVideoParams params) throws Exception {

        String loginId = (String) StpUtil.getLoginId();

        MUser user = authServices.getUserInfo(loginId).getData();

        Map<MultipartFile, FileInfo> fileInfoMap = postAssetsService.uploadFiles(params.getFiles());

        PostModuleInfo postModuleInfo = new PostModuleInfo();
        postModuleInfo.setUserInfo(user);
        postModuleInfo.setTypeId("3");
        postModuleInfo.setDesc(params.getContent());
        postModuleInfo.setVideoInfoId(params.getVideoInfoId());
        postModuleInfo.setChildPostId(null);

        String moduleId = this.savePostModule(postModuleInfo);

        postAssetsService.savePostImg(fileInfoMap,moduleId);


        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setModuleId(moduleId);
        post.setTopicId("-1");
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

        ResponseResult<Avatar> responseResult = authServices.getAvatarInfo(userInfo.getAvatarId());
        if (responseResult.getCode()!=200){
            log.error("远程调用失败,Main--->Auth");
            throw new Exception("远程调用失败,Main--->Auth");
        }
        //获取用户头像
        Avatar avatar = responseResult.getData();
        //保存模块信息
        postModule.setUserAvatarPath(avatar.getPath());
        postModule.setUserAvatarUrl(userInfo.getAvatarUrl());
        postModule.setUserAvatarBucket(avatar.getBucket());
        postModule.setUserNickname(userInfo.getNickname());
        postModule.setDescription(postModuleInfo.getDesc());
        postModule.setTypeId(postModuleInfo.getTypeId());
        postModule.setVideoInfoId(postModuleInfo.getVideoInfoId());
        postModule.setChildPostId(postModuleInfo.getChildPostId());
        boolean save = postModuleService.save(postModule);

        if (!save) {
            throw new Exception("not save");
        }

        return postModule.getId();

    }



    @Async(value = "ThreadPool")
    public void updatePostLikeCount(String postId,Integer likeCount){
        postService.update(new LambdaUpdateWrapper<Post>().eq(Post::getId,postId).set(Post::getLikeCount,likeCount));
    }


    /**
     * 删除动态（包括评论，module，图片）
     * @param postId 动态id
     * @return boolean
     */
    public boolean deletePost(String postId){
        log.info("开始删除post:"+postId);
        Post post = postService.getById(postId);
        boolean allCommentRM = deletePostCommentByPostId(postId);
        boolean moduleRM = deleteModule(post.getModuleId());
        boolean postRM = postService.removeById(postId);
        deletePostImg(post.getModuleId());
        return allCommentRM && moduleRM && postRM;
    }


    //删除module
    public boolean deleteModule(String moduleId){
        return postModuleService.removeById(moduleId);
    }


    //删除Post下的评论及其相关内容
    public boolean deletePostCommentByPostId(String postId){

        log.info("开始删除Post的评论相关内容,postId:"+postId);
        //从Post找直接回复的评论
        List<String> commentIdsToDelete = commentToPostService.list(new LambdaQueryWrapper<PostCommentToPost>()
                .eq(PostCommentToPost::getPostId, postId)
                .select(PostCommentToPost::getCommentId))
                .stream()
                .map(PostCommentToPost::getCommentId)
                .collect(Collectors.toList());

        return deletePostCommentByCommentId(commentIdsToDelete);

    }


    //删除Post携带的图片
    public void deletePostImg(String moduleId){
        log.info("开始删除moduleId对应的图片,moduleId:"+moduleId);
        postAssetsService.deletePostImg(moduleId);
    }


    /**
     * 删除和commentIds有关的一切记录
     * @param commentIds 评论id
     * @return boolean
     */
    @Transactional
    public boolean deletePostCommentByCommentId(List<String> commentIds){
        log.info("开始删除commentIds对应的相关内容"+commentIds);
        //找与commentId有关联的评论
        List<String> ids = commentReplyCommentService.list(new LambdaQueryWrapper<PostCommentReplyComment>()
                        .in(PostCommentReplyComment::getFatherCommentId, commentIds)
                        .select(PostCommentReplyComment::getCommentId)
                        .select(PostCommentReplyComment::getReplyCommentId))
                .stream()
                .flatMap(item -> Stream.of(item.getCommentId(), item.getReplyCommentId()))
                .collect(Collectors.toList());

        commentIds.addAll(ids);

        List<String> idsToDelete = commentIds.stream().distinct().collect(Collectors.toList());

        boolean commentRM = postCommentService.removeBatchByIds(commentIds);
        boolean commentToPostRM =  commentToPostService.remove(new LambdaQueryWrapper<PostCommentToPost>().in(PostCommentToPost::getCommentId,commentIds));
        boolean commentReplyRm =  commentReplyCommentService.remove(new LambdaQueryWrapper<PostCommentReplyComment>()
                .in(PostCommentReplyComment::getCommentId,commentIds)
                .in(PostCommentReplyComment::getFatherCommentId,commentIds)
                .in(PostCommentReplyComment::getReplyCommentId,commentIds));

       return commentRM && commentReplyRm && commentToPostRM;

    }





}
