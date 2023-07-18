package com.ducksoup.dilivideomain.Controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.AuthEntity.MUser;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideofeign.Auth.AuthServices;
import com.ducksoup.dilivideomain.Controller.Params.PostCommentParams;
import com.ducksoup.dilivideomain.Controller.Params.ReplyPostCommentParams;
import com.ducksoup.dilivideomain.Entity.PostCommentReplyComment;
import com.ducksoup.dilivideomain.Entity.PostCommentToPost;
import com.ducksoup.dilivideomain.service.PostCommentReplyCommentService;
import com.ducksoup.dilivideomain.service.PostCommentService;
import com.ducksoup.dilivideomain.service.PostCommentToPostService;
import com.ducksoup.dilivideomain.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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


    /**
     * 回复动态
     * @param params PostCommentParams
     * @return ResponseResult<Boolean>
     */
    @SaCheckLogin
    @PostMapping("/reply_to_post")
    public ResponseResult<Boolean> replyToPost(@RequestBody PostCommentParams params){
        String loginId = (String) StpUtil.getLoginId();

        //获取用户信息
        ResponseResult<MUser> userInfoRes = authServices.getUserInfo(loginId);

        if (userInfoRes.getCode()!=200){
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
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"服务器内部错误",false);
        }

        if (postCommentId==null){
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"服务器内部错误",false);
        }

        PostCommentToPost postCommentToPost = new PostCommentToPost();
        postCommentToPost.setId(UUID.randomUUID().toString());
        postCommentToPost.setCommentId(postCommentId);
        postCommentToPost.setPostId(params.getPostId());
        postCommentToPost.setStatus(1);

        //保存评论动态映射
        boolean save = postCommentToPostService.save(postCommentToPost);

        if (!save){
            postCommentService.removeById(postCommentId);
            log.error("保存postCommentToPost失败，删除postComment");
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"服务器内部错误",false);
        }

        postService.increasePostCommentCount(params.getPostId());

        return  new ResponseResult<>(HttpStatus.HTTP_OK, "评论成功", true);
    }



    @SaCheckLogin
    @PostMapping("/reply_to_post_comment")
    public ResponseResult<Boolean> replyToPostComment(@RequestBody ReplyPostCommentParams params){

        String loginId = (String) StpUtil.getLoginId();

        //获取用户信息
        ResponseResult<MUser> userInfoRes = authServices.getUserInfo(loginId);
        if (userInfoRes.getCode()!=200){
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
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"服务器内部错误",false);
        }


        PostCommentReplyComment postCommentReplyComment = new PostCommentReplyComment();
        postCommentReplyComment.setId(UUID.randomUUID().toString());
        postCommentReplyComment.setFatherCommentId(params.getFatherId());
        postCommentReplyComment.setCommentId(params.getReplyToId());
        postCommentReplyComment.setReplyCommentId(postCommentId);
        postCommentReplyComment.setIfdirect(params.getReplyToId().equals(params.getFatherId()) ? 1 : 0);
        postCommentReplyComment.setStatus(1);

        boolean save = postCommentReplyCommentService.save(postCommentReplyComment);

        if (!save){
            postCommentService.removeById(postCommentId);
            log.error("保存PostCommentReplyComment失败，删除postComment");
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"服务器内部错误",false);
        }

        postService.increasePostCommentCount(params.getPostId());

        return  new ResponseResult<>(HttpStatus.HTTP_OK, "评论成功", true);


    }

}
