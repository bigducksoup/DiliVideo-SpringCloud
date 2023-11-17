package com.ducksoup.dilivideomain.mainservices;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideomain.entity.PostComment;
import com.ducksoup.dilivideomain.entity.PostCommentReplyComment;
import com.ducksoup.dilivideomain.service.PostCommentReplyCommentService;
import com.ducksoup.dilivideomain.service.PostCommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MyPostCommentService {

    private final PostCommentReplyCommentService postCommentReplyCommentService;

    private final PostCommentService postCommentService;

    public Map<String, List<PostComment>> queryChildPostComments(List<String> fatherCommentIds){
        Map<String,List<PostComment>> fatherId_ChildCommentList = new HashMap<>();


        for (String id : fatherCommentIds) {
            List<PostComment> children = new ArrayList<>();
            fatherId_ChildCommentList.put(id,children);
        }

        //获取父评论的子评论
        List<PostCommentReplyComment> fatherIdReplyId = postCommentReplyCommentService.list(
                new LambdaQueryWrapper<PostCommentReplyComment>()
                        .in(PostCommentReplyComment::getFatherCommentId, fatherCommentIds)
                        .apply("post_comment_reply_comment.father_comment_id = post_comment_reply_comment.comment_id")
                        .apply("(SELECT COUNT(*) FROM post_comment_reply_comment p2 WHERE post_comment_reply_comment.father_comment_id=p2.father_comment_id)<=5")
        );


        if (!fatherIdReplyId.isEmpty()){

            //query child comments
            List<String> childCommentIds = fatherIdReplyId.stream().map(PostCommentReplyComment::getReplyCommentId).collect(Collectors.toList());
            List<PostComment> childComments = postCommentService.list(new LambdaQueryWrapper<PostComment>().in(PostComment::getId, childCommentIds));

            //mapping father comment and child comment id
            Map<String, List<PostCommentReplyComment>> mapping = fatherIdReplyId.stream().collect(Collectors.groupingBy(PostCommentReplyComment::getFatherCommentId));

            //mapping child comment key=id val=childComment
            Map<String, PostComment> childCommentMap = childComments.stream().collect(Collectors.toMap(PostComment::getId, item -> item));


            fatherId_ChildCommentList.forEach((k,v)->{
                List<PostCommentReplyComment> replyCommentsMapping = mapping.get(k);
                if (replyCommentsMapping!=null){
                    for (PostCommentReplyComment item : replyCommentsMapping) {
                        PostComment postComment = childCommentMap.get(item.getReplyCommentId());
                        v.add(postComment);
                    }
                }
            });

        }


        return fatherId_ChildCommentList;
    }


}
