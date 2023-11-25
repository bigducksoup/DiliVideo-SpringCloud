package com.ducksoup.dilivideotext.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideoentity.auth.MUser;
import com.ducksoup.dilivideoentity.content.Videoinfo;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import com.ducksoup.dilivideofeign.content.ContentServices;
import com.ducksoup.dilivideotext.controller.params.CommentDeleteParams;
import com.ducksoup.dilivideotext.controller.params.CommentParams;
import com.ducksoup.dilivideotext.controller.params.ReplyCommentParams;
import com.ducksoup.dilivideotext.entity.Comment;
import com.ducksoup.dilivideotext.entity.CommentReplyComment;
import com.ducksoup.dilivideotext.entity.CommentVideoinfo;
import com.ducksoup.dilivideotext.mainservices.action.CommentLikeService;
import com.ducksoup.dilivideotext.mainservices.VideoCommentService;
import com.ducksoup.dilivideotext.utils.OSSUtils;
import com.ducksoup.dilivideotext.service.CommentReplyCommentService;
import com.ducksoup.dilivideotext.service.CommentService;
import com.ducksoup.dilivideotext.service.CommentVideoinfoService;
import com.ducksoup.dilivideotext.vo.CommentChild;
import com.ducksoup.dilivideotext.vo.CommentItemVo;
import com.ducksoup.dilivideotext.vo.ReplyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private ContentServices contentServices;

    @Autowired
    private AuthServices authServices;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentVideoinfoService commentVideoinfoService;

    @Autowired
    private CommentReplyCommentService replyCommentService;

    @Resource
    private CommentLikeService commentLikeService;

    @Autowired
    private OSSUtils ossUtils;


    /**
     * 视频下发评论
     *
     * @param commentParams 参数
     * @return ResponseResult<Boolean>
     */
    @SaCheckLogin
    @PostMapping("/replyToVideo")
    public ResponseResult<Boolean> replyToVideo(@RequestBody @Valid CommentParams commentParams) {

        String loginId = (String) StpUtil.getLoginId();


        //远程调用获取user，videoInfo
        ResponseResult<MUser> userInfoRes = authServices.getUserInfo(loginId);
        ResponseResult<Videoinfo> videoInfoByIdRes = contentServices.getVideoInfoById(commentParams.getVideoInfoId());


        if (userInfoRes.getCode() != 200 || videoInfoByIdRes.getCode() != 200) {
            log.error("远程调用失败：main->auth or main->content");
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }

        MUser user = userInfoRes.getData();
        Videoinfo videoinfo = videoInfoByIdRes.getData();

        String commentId = null;
        try {
            commentId = commentService.saveComment(commentParams.getContent(), user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }

        if (commentId == null) {
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }


        CommentVideoinfo commentVideoinfo = new CommentVideoinfo();
        commentVideoinfo.setId(UUID.randomUUID().toString());
        commentVideoinfo.setCommentId(commentId);
        commentVideoinfo.setVideoinfoId(videoinfo.getId());
        commentVideoinfo.setStatus(1);
        //保存评论与视频对应信息
        boolean save = commentVideoinfoService.save(commentVideoinfo);

        return save ?
                new ResponseResult<>(HttpStatus.HTTP_OK, "评论成功", true) :
                new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "评论失败", false);
    }


    /**
     * 回复评论
     *
     * @param params 回复评论的参数
     * @return ResponseResult<Boolean>
     */

    @SaCheckLogin
    @PostMapping("/replyToComment")
    public ResponseResult<Boolean> replyToComment(@RequestBody @Valid ReplyCommentParams params) {
        String loginId = (String) StpUtil.getLoginId();
        ResponseResult<MUser> userInfoRes = authServices.getUserInfo(loginId);
        if (userInfoRes.getCode() != 200) {
            log.error("远程调用失败：main->auth");
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }


        MUser user = userInfoRes.getData();

        String commentId = null;
        try {
            commentId = commentService.saveComment(params.getContent(), user);
        } catch (Exception e) {
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }


        if (commentId == null) {
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }

        CommentReplyComment commentReplyComment = new CommentReplyComment();
        commentReplyComment.setId(UUID.randomUUID().toString());
        commentReplyComment.setFatherCommentId(params.getFatherId());
        commentReplyComment.setCommentId(params.getReplyToId());
        commentReplyComment.setReplyCommentId(commentId);
        commentReplyComment.setIfdirect(params.getIfDirect());
        commentReplyComment.setStatus(1);

        boolean save = replyCommentService.save(commentReplyComment);

        return save ?
                new ResponseResult<>(HttpStatus.HTTP_OK, "评论成功", true) :
                new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "评论失败", false);
    }


    /**
     * @Warn 已废弃
     * 查询视频信息下的评论
     * videoInfoId
     * mode : 查询模式：0为按时间，1为按热度
     *
     * @return ResponseResult
     */
    @GetMapping("/v1/comment_item")
    public ResponseResult<List<CommentItemVo>> commentItem(@RequestParam @NotNull String videoInfoId, @RequestParam @NotNull Integer mode, @NotNull @RequestParam Integer page) {

        Integer pageSize = 20;

        //通过videoinfoid查询commentId
        List<String> commentIds =
                mode == 1 ?
                        commentService.queryCommentIdsByVideoInfoIdSortByLikeCount(videoInfoId, page, pageSize)
                        :
                        commentService.queryCommentIdsByVideoInfoIdSortByTime(videoInfoId, page, pageSize);

        if (commentIds.size() == 0) {
            return new ResponseResult<>(HttpStatus.HTTP_OK, "没有评论", new ArrayList<CommentItemVo>());
        }
        //获取评论

        List<Comment> comments = commentService.listByIds(commentIds);


        //回复id映射
        List<CommentReplyComment> replyComments = replyCommentService.list(new LambdaQueryWrapper<CommentReplyComment>()
                .in(CommentReplyComment::getCommentId, commentIds));

        //map<父评论id,[回复父评论的id映射] 分类子评论id
        Map<String, List<CommentReplyComment>> collect = replyComments.stream().collect(Collectors.groupingBy(i -> {
                    for (String fId : commentIds) {
                        if (i.getCommentId().equals(fId)) {
                            return fId;
                        }
                    }
                    return "none";
                }
        ));

        List<String> replyIds = replyComments.stream()
                .map(CommentReplyComment::getReplyCommentId)
                .collect(Collectors.toList());

        //获取回复
        List<Comment> replys = new ArrayList<>();
        if (replyIds.size() != 0) {
            replys = commentService.listByIds(replyIds).stream()
                    .sorted(Comparator.comparing(Comment::getCreateTime))
                    .collect(Collectors.toList());
        }


        List<CommentItemVo> commentItemVos = new ArrayList<>();

        //遍历父评论
        for (Comment c : comments) {
            CommentItemVo vo = new CommentItemVo();
            BeanUtil.copyProperties(c, vo);

            vo.setUserAvatarUrl(ossUtils.makeUrl(vo.getUserAvatarBucket(), vo.getUserAvatarPath()));

            vo.setChildren(new ArrayList<>());
            //获取子评论ids
            List<CommentReplyComment> commentReplyComments = collect.get(c.getId());

            if (commentReplyComments != null) {
                //获取子评论的ids
                List<String> ids = commentReplyComments.stream().map(CommentReplyComment::getReplyCommentId).collect(Collectors.toList());
                //获取子评论Entity
                List<Comment> children = replys.stream().filter(i -> ids.contains(i.getId())).collect(Collectors.toList());
                List<CommentChild> childVoList = new ArrayList<>();
                for (Comment item : children) {
                    if (childVoList.size() > 5) break;
                    CommentChild child = new CommentChild();
                    child.setId(item.getId());
                    child.setUserId(item.getUserId());
                    child.setNickName(item.getUserNickname());
                    child.setContent(item.getContent());
                    childVoList.add(child);
                }
                vo.setChildren(childVoList);
            }


            commentItemVos.add(vo);
        }

        if (mode == 1) {
            commentItemVos = commentItemVos.stream().sorted(Comparator.comparing(CommentItemVo::getLikeCount).reversed()).collect(Collectors.toList());
        } else {
            commentItemVos = commentItemVos.stream().sorted(Comparator.comparing(CommentItemVo::getCreateTime).reversed()).collect(Collectors.toList());
        }


        return new ResponseResult<>(HttpStatus.HTTP_OK, "评论获取成功", commentItemVos);

    }


    @Resource
    private VideoCommentService videoCommentService;



    //TODO 缓存优化
    @GetMapping("/comment_item")
    public ResponseResult<List<CommentItemVo>> commentItem2(@RequestParam @NotNull String videoInfoId, @RequestParam @NotNull Integer mode, @RequestParam @NotNull Integer page) {


        List<Comment> comments = videoCommentService.queryCommentByVideoInfoId(videoInfoId, mode, page, 20);

        List<CommentItemVo> res = new ArrayList<>();

        if (comments.isEmpty()){
            return new ResponseResult<>(HttpStatus.HTTP_OK,"success",res);
        }

        List<String> fatherCommentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());


        Map<String, List<Comment>> fatherId_ChildCommentList = videoCommentService.queryChildComment(fatherCommentIds);


        for (Comment comment : comments) {

            CommentItemVo itemVo = new CommentItemVo();
            BeanUtil.copyProperties(comment,itemVo);
            List<CommentChild> childVos = new ArrayList<>();

            List<Comment> childComments = fatherId_ChildCommentList.get(comment.getId());
            for (Comment childComment : childComments) {
                CommentChild child = new CommentChild();
                child.setId(childComment.getId());
                child.setUserId(childComment.getUserId());
                child.setNickName(childComment.getUserNickname());
                child.setContent(childComment.getContent());
                childVos.add(child);
            }
            itemVo.setChildren(childVos);
            res.add(itemVo);

        }

        String loginId = null;

        if (StpUtil.isLogin()){
            loginId = (String) StpUtil.getLoginId();
        }
        log.info("设置视频评论点赞情况");
        commentLikeService.setLikeStatus(res,loginId);


        return new ResponseResult<>(HttpStatus.HTTP_OK,"success",res);


    }


    @GetMapping("/comment_reply")
    public ResponseResult<List<ReplyVo>> commentReply(@RequestParam @NotNull String fatherCommentId) {
        List<ReplyVo> replyVos = new ArrayList<>();
        List<CommentReplyComment> commentReplyCommentList = replyCommentService.list(new LambdaQueryWrapper<CommentReplyComment>().eq(CommentReplyComment::getFatherCommentId, fatherCommentId));

        if (commentReplyCommentList.size() != 0) {
            List<String> replyIds = commentReplyCommentList.stream().map(CommentReplyComment::getReplyCommentId).collect(Collectors.toList());
            List<String> beRepliedIds = commentReplyCommentList.stream().map(CommentReplyComment::getCommentId).collect(Collectors.toList());
            Map<String, Comment> replies = commentService.listByIds(replyIds).stream().collect(Collectors.toMap(Comment::getId, c -> c));
            Map<String, Comment> comment = commentService.listByIds(beRepliedIds).stream().collect(Collectors.toMap(Comment::getId, c -> c));


            List<ReplyVo> finalReplyVos = replyVos;
            commentReplyCommentList.forEach(item -> {

                Comment rep = replies.get(item.getReplyCommentId());
                Comment com = comment.get(item.getCommentId());

                ReplyVo replyVo = new ReplyVo();
                replyVo.setId(rep.getId());
                replyVo.setReplierId(rep.getUserId());
                replyVo.setAvatar(ossUtils.makeUrl(rep.getUserAvatarBucket(), rep.getUserAvatarPath()));
                replyVo.setReplierName(rep.getUserNickname());
                replyVo.setLevel(rep.getUserLevel());
                replyVo.setToId(com.getUserId());
                replyVo.setToName(com.getUserNickname());
                replyVo.setContent(rep.getContent());
                replyVo.setTime(rep.getCreateTime());
                replyVo.setLikeCount(rep.getLikeCount());
                finalReplyVos.add(replyVo);
            });

            replyVos = finalReplyVos.stream().sorted(Comparator.comparing(ReplyVo::getTime)).collect(Collectors.toList());

        }


        String loginId = null;
        if (StpUtil.isLogin()){
            loginId = (String) StpUtil.getLoginId();
        }

        commentLikeService.setReplyLikeStatus(replyVos,loginId);


        return new ResponseResult<>(HttpStatus.HTTP_OK, "获取回复成功", replyVos);

    }


    /**
     * delete comment
     *
     * @param params CommentDeleteParams
     * @return boolean
     */
    @SaCheckLogin
    @PostMapping("/delete")
    public ResponseResult<Boolean> deleteComment(@RequestBody @Valid CommentDeleteParams params) {

        String loginId = (String) StpUtil.getLoginId();

        Comment comment = commentService.getOne(new LambdaQueryWrapper<Comment>().eq(Comment::getId, params.getCommentId()).select(Comment::getUserId));

        if (comment == null) {
            return new ResponseResult<>(HttpStatus.HTTP_OK, "该评论不存在", false);
        }

        if (!comment.getUserId().equals(loginId)) {
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN, "没有权限删除该评论", false);
        }

        boolean deleted = commentService.deleteVideoComment(params.getCommentId());

        if (deleted) {
            return new ResponseResult<>(HttpStatus.HTTP_OK, "删除评论成功", true);
        }

        return new ResponseResult<>(HttpStatus.HTTP_OK, "删除评论失败", false);


    }


}
