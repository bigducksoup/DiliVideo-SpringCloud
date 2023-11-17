package com.ducksoup.dilivideomain.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ducksoup.dilivideoentity.auth.MUser;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import com.ducksoup.dilivideomain.controller.params.CommentDeleteParams;
import com.ducksoup.dilivideomain.controller.params.PostCommentParams;
import com.ducksoup.dilivideomain.controller.params.ReplyPostCommentParams;
import com.ducksoup.dilivideomain.entity.Comment;
import com.ducksoup.dilivideomain.entity.PostComment;
import com.ducksoup.dilivideomain.entity.PostCommentReplyComment;
import com.ducksoup.dilivideomain.entity.PostCommentToPost;
import com.ducksoup.dilivideomain.mainservices.MyPostCommentService;
import com.ducksoup.dilivideomain.utils.OSSUtils;
import com.ducksoup.dilivideomain.dto.IdMap;
import com.ducksoup.dilivideomain.service.PostCommentReplyCommentService;
import com.ducksoup.dilivideomain.service.PostCommentService;
import com.ducksoup.dilivideomain.service.PostCommentToPostService;
import com.ducksoup.dilivideomain.service.PostService;
import com.ducksoup.dilivideomain.vo.CommentChild;
import com.ducksoup.dilivideomain.vo.PostCommentVo;
import com.ducksoup.dilivideomain.vo.ReplyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/post_comment")
public class PostCommentController {

    @Autowired
    private PostCommentService postCommentService;

    @Autowired
    private PostService postService;


    @Autowired
    private PostCommentToPostService postCommentToPostService;


    @Autowired
    private PostCommentReplyCommentService postCommentReplyCommentService;

    @Autowired
    private AuthServices authServices;


    @Autowired
    private OSSUtils ossUtils;


    @Resource
    private MyPostCommentService myPostCommentService;


    /**
     * 回复动态
     *
     * @param params PostCommentParams
     * @return ResponseResult<Boolean>
     */
    @SaCheckLogin
    @PostMapping("/reply_to_post")
    public ResponseResult<Boolean> replyToPost(@RequestBody @Valid PostCommentParams params) {
        String loginId = (String) StpUtil.getLoginId();

        //获取用户信息
        ResponseResult<MUser> userInfoRes = authServices.getUserInfo(loginId);

        if (userInfoRes.getCode() != 200) {
            log.error("远程调用失败：main->auth or main->content");
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }

        MUser user = userInfoRes.getData();
        String postCommentId = null;
        try {
            //保存评论
            postCommentId = postCommentService.savePostComment(params.getContent(), user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }

        if (postCommentId == null) {
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }

        PostCommentToPost postCommentToPost = new PostCommentToPost();
        postCommentToPost.setId(UUID.randomUUID().toString());
        postCommentToPost.setCommentId(postCommentId);
        postCommentToPost.setPostId(params.getPostId());
        postCommentToPost.setStatus(1);

        //保存评论动态映射
        boolean save = postCommentToPostService.save(postCommentToPost);

        if (!save) {
            postCommentService.removeById(postCommentId);
            log.error("保存postCommentToPost失败，删除postComment");
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }

        postService.increasePostCommentCount(params.getPostId());

        return new ResponseResult<>(HttpStatus.HTTP_OK, "评论成功", true);
    }


    @SaCheckLogin
    @PostMapping("/reply_to_post_comment")
    public ResponseResult<Boolean> replyToPostComment(@RequestBody @Valid ReplyPostCommentParams params) {

        String loginId = (String) StpUtil.getLoginId();

        //获取用户信息
        ResponseResult<MUser> userInfoRes = authServices.getUserInfo(loginId);
        if (userInfoRes.getCode() != 200) {
            log.error("远程调用失败：main->auth or main->content");
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }

        MUser user = userInfoRes.getData();
        String postCommentId = null;
        try {
            //保存评论
            postCommentId = postCommentService.savePostComment(params.getContent(), user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }

        //保存回复与评论的id对应关系
        PostCommentReplyComment postCommentReplyComment = new PostCommentReplyComment();
        postCommentReplyComment.setId(UUID.randomUUID().toString());
        postCommentReplyComment.setFatherCommentId(params.getFatherId());
        postCommentReplyComment.setCommentId(params.getReplyToId());
        postCommentReplyComment.setReplyCommentId(postCommentId);
        postCommentReplyComment.setIfdirect(params.getReplyToId().equals(params.getFatherId()) ? 1 : 0);
        postCommentReplyComment.setStatus(1);

        boolean save = postCommentReplyCommentService.save(postCommentReplyComment);

        if (!save) {
            postCommentService.removeById(postCommentId);
            log.error("保存PostCommentReplyComment失败，删除postComment");
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }

        postService.increasePostCommentCount(params.getPostId());

        return new ResponseResult<>(HttpStatus.HTTP_OK, "评论成功", true);


    }


    // TODO test
    @GetMapping("/get_comments")
    public ResponseResult<List<PostCommentVo>> getComments(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam String postId, @RequestParam boolean orderByTime) {

        List<PostComment> postComments = postCommentService.queryPostCommentByPostId(postId, page, pageSize, orderByTime);


        if (postComments.size() == 0) {
            return new ResponseResult<>(HttpStatus.HTTP_OK, "暂无评论", new ArrayList<>());
        }

        List<String> fatherCommentIds = postComments.stream().map(PostComment::getId).collect(Collectors.toList());


        Map<String, List<PostComment>> fatherId_ChildCommentList = myPostCommentService.queryChildPostComments(fatherCommentIds);


        List<PostCommentVo> response = new ArrayList<>();

        for (PostComment item : postComments) {

            PostCommentVo postCommentVo = new PostCommentVo();
            BeanUtil.copyProperties(item, postCommentVo);
            postCommentVo.setUserAvatarUrl(ossUtils.makeUrl(item.getUserAvatarBucket(), item.getUserAvatarPath()));

            List<CommentChild> childVos = new ArrayList<>();

            List<PostComment> childComments = fatherId_ChildCommentList.get(item.getId());

            for (PostComment childComment : childComments) {
                CommentChild commentChild = new CommentChild();
                commentChild.setId(childComment.getId());
                commentChild.setUserId(childComment.getUserId());
                commentChild.setNickName(childComment.getUserNickname());
                commentChild.setContent(childComment.getContent());
                childVos.add(commentChild);
            }
            postCommentVo.setChild(childVos);


            response.add(postCommentVo);
        }


        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取评论成功", response);

    }


    @GetMapping("/get_comment_reply")
    public ResponseResult<List<ReplyVo>> getCommentReply(@RequestParam Integer page, @RequestParam Integer pageSize,@RequestParam String commentId,@RequestParam boolean orderByTime){

        List<IdMap> commentIdReplyId = postCommentService.queryPostCommentReplies(commentId, page, pageSize, orderByTime);

        if (commentIdReplyId.size()==0){
            return new ResponseResult<>(HttpStatus.HTTP_OK,"暂无评论",new ArrayList<>());
        }

        //被回复的评论id
        List<String> commentIds = commentIdReplyId.stream().map(IdMap::getId).collect(Collectors.toList());
        //回复id
        List<String> replyIds = commentIdReplyId.stream().map(IdMap::getMapId).collect(Collectors.toList());


        List<PostComment> comments = postCommentService.list(new LambdaQueryWrapper<PostComment>().in(PostComment::getId, commentIds));

        List<PostComment> replies = postCommentService.list(new LambdaQueryWrapper<PostComment>().in(PostComment::getId, replyIds));

        //key:PostComment::Id value:PostComment
        Map<String, PostComment> commentMap = comments.stream().collect(Collectors.toMap(PostComment::getId, e -> e));

        //key:PostComment::Id value:PostComment
        Map<String, PostComment> replyMap = replies.stream().collect(Collectors.toMap(PostComment::getId, e -> e));

        List<ReplyVo> replyVos = new ArrayList<>();

        for (IdMap idMap : commentIdReplyId){
            PostComment comment = commentMap.get(idMap.getId());
            PostComment reply = replyMap.get(idMap.getMapId());

            ReplyVo replyVo = new ReplyVo();
            replyVo.setId(reply.getId());
            replyVo.setReplierId(reply.getUserId());
            replyVo.setAvatar(ossUtils.makeUrl(reply.getUserAvatarBucket(),reply.getUserAvatarPath()));
            replyVo.setReplierName(reply.getUserNickname());
            replyVo.setLevel(reply.getUserLevel());
            replyVo.setToId(comment.getId());
            replyVo.setToName(comment.getUserNickname());
            replyVo.setContent(reply.getContent());
            replyVo.setTime(reply.getCreateTime());
            replyVo.setLikeCount(reply.getLikeCount());

            replyVos.add(replyVo);

        }
        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取回复成功",replyVos);

    }


    /**
     * delete post comment
     * @param params CommentDeleteParams
     * @return boolean
     * TODO test
     */
    @PostMapping("/delete")
    public ResponseResult<Boolean> delete(@RequestBody @Valid CommentDeleteParams params){

        String loginId = (String) StpUtil.getLoginId();

        PostComment postComment = Db.lambdaQuery(PostComment.class).eq(PostComment::getId, params.getCommentId()).select(PostComment::getUserId).one();


        if (postComment==null){
            return  new ResponseResult<>(HttpStatus.HTTP_OK, "该评论不存在", false);
        }

        if (!postComment.getUserId().equals(loginId)){
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN, "没有权限删除该评论", false);
        }

        boolean deleted = postCommentService.deletePostComment(params.getCommentId());


        if (deleted) {
            return new ResponseResult<>(HttpStatus.HTTP_OK, "删除评论成功", true);
        }

        return new ResponseResult<>(HttpStatus.HTTP_OK, "删除评论失败", false);

    }



}
