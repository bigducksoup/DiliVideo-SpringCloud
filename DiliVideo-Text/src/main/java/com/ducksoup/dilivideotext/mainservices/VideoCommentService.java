package com.ducksoup.dilivideotext.mainservices;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideotext.entity.Comment;
import com.ducksoup.dilivideotext.entity.CommentReplyComment;
import com.ducksoup.dilivideotext.mapper.CommentVideoinfoMapper;
import com.ducksoup.dilivideotext.service.CommentReplyCommentService;
import com.ducksoup.dilivideotext.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VideoCommentService {


    @Resource
    private CommentVideoinfoMapper commentVideoinfoMapper;

    @Resource
    private CommentReplyCommentService replyCommentService;

    @Resource
    private CommentService commentService;


    public List<Comment> queryCommentByVideoInfoId(String videoInfoId,int mode,int page,int pageSize){
        return commentVideoinfoMapper.queryCommentByVideoInfoId(videoInfoId,mode,(page-1)*pageSize,pageSize);
    }


    public Map<String,List<Comment>> queryChildComment(List<String> fatherCommentIds){

        Map<String,List<Comment>> fatherId_ChildCommentList = new HashMap<>();


        for (String id : fatherCommentIds) {
            List<Comment> children = new ArrayList<>();
            fatherId_ChildCommentList.put(id,children);
        }


        //查询子评论Id
        List<CommentReplyComment> commentReplyComments = replyCommentService.list(
                new LambdaQueryWrapper<CommentReplyComment>()
                        .in(CommentReplyComment::getFatherCommentId, fatherCommentIds)
                        .apply("m_comment_reply_comment.father_comment_id = m_comment_reply_comment.comment_id")
                        .apply("(SELECT COUNT(*) FROM m_comment_reply_comment m2 WHERE m_comment_reply_comment.father_comment_id=m2.father_comment_id)<=5")

        );


        if (!commentReplyComments.isEmpty()){
            List<String> childIds = commentReplyComments.stream().map(CommentReplyComment::getReplyCommentId).collect(Collectors.toList());

            List<Comment> childComments = commentService.list(new LambdaQueryWrapper<Comment>().in(Comment::getId, childIds));

            Map<String, List<CommentReplyComment>> fatherId_List = commentReplyComments.stream().collect(Collectors.groupingBy(CommentReplyComment::getFatherCommentId));

            Map<String, Comment> childCommentMap = childComments.stream().collect(Collectors.toMap(Comment::getId, item -> item));

            fatherId_ChildCommentList.forEach((k,v)->{
                List<CommentReplyComment> list = fatherId_List.get(k);
                if (list!=null){
                    for (CommentReplyComment mapping : list) {
                        Comment childComment = childCommentMap.get(mapping.getReplyCommentId());
                        v.add(childComment);
                    }
                }
            });
        }

        return fatherId_ChildCommentList;


    }



}
