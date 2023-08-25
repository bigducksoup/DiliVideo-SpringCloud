package com.ducksoup.dilivideomain.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideomain.controller.params.LikeActionParams;
import com.ducksoup.dilivideomain.mainservices.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/action")
public class ActionController {


    @Autowired
    private ActionService actionService;

    /**
     * 对动态，动态的评论，视频评论进行点赞（或取消）
     * @param params  targetType:要点赞（或取消）的目标，
     *                0:Post动态
     *                1:PostComment动态评论
     *                2:Comment视频评论
     *
     *                targetId:目标ID
     * @return ResponseResult<Boolean>
     */
    @SaCheckLogin
    @PostMapping("/like")
    public ResponseResult<Boolean> likeAction(@Valid @RequestBody LikeActionParams params){

        boolean action = actionService.dispatchAction(params.getTargetType(), params.getTargetId());

        return new ResponseResult<>(HttpStatus.HTTP_OK,"操作成功",action);

    }



}
