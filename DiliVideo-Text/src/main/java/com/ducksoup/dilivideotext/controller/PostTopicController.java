package com.ducksoup.dilivideotext.controller;


import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideotext.entity.PostTopic;
import com.ducksoup.dilivideotext.service.PostTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/post_topic")
public class PostTopicController {

    @Autowired
    private PostTopicService postTopicService;

    /**
     * 查询话题列表
     * @param page 页数
     * @return ResponseResult<List<PostTopic>>
     */
    @GetMapping("/topic_list")
    public ResponseResult<List<PostTopic>> getTopicList(@RequestParam Integer page) {
        Page<PostTopic> pager = new Page<>(page, 50);

        LambdaQueryWrapper<PostTopic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostTopic::getStatus, 1);

        List<PostTopic> topics = postTopicService.page(pager, queryWrapper).getRecords();

        return new ResponseResult<>(HttpStatus.HTTP_OK, "查询成功", topics);
    }

}
