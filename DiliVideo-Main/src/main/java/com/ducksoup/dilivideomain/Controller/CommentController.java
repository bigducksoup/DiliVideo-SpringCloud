package com.ducksoup.dilivideomain.Controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ducksoup.dilivideoentity.AuthEntity.MUser;
import com.ducksoup.dilivideoentity.ContentEntity.Videoinfo;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideofeign.Auth.AuthServices;
import com.ducksoup.dilivideofeign.Content.ContentServices;
import com.ducksoup.dilivideomain.Controller.Params.CommentParams;
import com.ducksoup.dilivideomain.Controller.Params.ReplyCommentParams;
import com.ducksoup.dilivideomain.Entity.Comment;
import com.ducksoup.dilivideomain.Entity.CommentReplyComment;
import com.ducksoup.dilivideomain.Entity.CommentVideoinfo;
import com.ducksoup.dilivideomain.service.CommentReplyCommentService;
import com.ducksoup.dilivideomain.service.CommentService;
import com.ducksoup.dilivideomain.service.CommentVideoinfoService;
import com.ducksoup.dilivideomain.vo.CommentChild;
import com.ducksoup.dilivideomain.vo.CommentItemVo;
import com.ducksoup.dilivideomain.vo.ReplyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    /**
     * 视频下发评论
     *
     * @param commentParams 参数
     * @return ResponseResult<Boolean>
     */
    @SaCheckLogin
    @PostMapping("/replyToVideo")
    public ResponseResult<Boolean> replyToVideo(@RequestBody CommentParams commentParams) {

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

        String commentId = commentService.saveComment(commentParams.getContent(), user);

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
    public ResponseResult<Boolean> replyToComment(@RequestBody ReplyCommentParams params) {
        String loginId = (String) StpUtil.getLoginId();
        ResponseResult<MUser> userInfoRes = authServices.getUserInfo(loginId);
        if (userInfoRes.getCode() != 200) {
            log.error("远程调用失败：main->auth");
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "服务器内部错误", false);
        }


        MUser user = userInfoRes.getData();
        String commentId = commentService.saveComment(params.getContent(), user);


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
     * 查询视频信息下的评论
     * videoInfoId
     * mode : 查询模式：0为按时间，1为按热度
     *
     * @return ResponseResult
     */
    @GetMapping("/comment_item")
    public ResponseResult<List<CommentItemVo>> commentItem(@RequestParam String videoInfoId, @RequestParam Integer mode, @RequestParam Integer page) {

        Page<CommentVideoinfo> pager = new Page<>(page, 20);

        //通过videoinfoid查询commentId
        List<String> commentIds = commentVideoinfoService.page(pager, new LambdaQueryWrapper<CommentVideoinfo>()
                        .eq(CommentVideoinfo::getVideoinfoId, videoInfoId)
                        .select(CommentVideoinfo::getCommentId)
                ).getRecords()
                .stream()
                .map(CommentVideoinfo::getCommentId)
                .collect(Collectors.toList());

        if (commentIds.size() == 0) {
            return new ResponseResult<>(HttpStatus.HTTP_OK, "没有评论", new ArrayList<CommentItemVo>());
        }
        //获取评论

        List<Comment> comments = commentService.listByIds(commentIds)
                .stream()
                .sorted(Comparator.comparing(Comment::getCreateTime).reversed())
                .collect(Collectors.toList());

        if (mode!=1){
            comments =  comments.stream().sorted(Comparator.comparing(Comment::getLikeCount)).collect(Collectors.toList());
        }

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


        return new ResponseResult<>(HttpStatus.HTTP_OK, "评论获取成功", commentItemVos);

    }


    @GetMapping("/comment_reply")
    public ResponseResult<List<ReplyVo>> commentReply(@RequestParam String fatherCommentId) {
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
                replyVo.setAvatar(rep.getUserAvatarUrl());
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


        return new ResponseResult<>(HttpStatus.HTTP_OK, "获取回复成功", replyVos);

    }


}
