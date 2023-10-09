package com.ducksoup.dilivideoauth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideoauth.controller.Params.FollowParam;

import com.ducksoup.dilivideoauth.entity.MUser;
import com.ducksoup.dilivideoauth.entity.RelationFollow;

import com.ducksoup.dilivideoauth.entity.UserSettings;
import com.ducksoup.dilivideoauth.mainServices.DesensitizationService;
import com.ducksoup.dilivideoauth.service.RelationFollowService;
import com.ducksoup.dilivideoauth.service.UserSettingsService;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideoentity.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/relation")
public class RelationController {


    @Autowired
    private RelationFollowService relationFollowService;

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private DesensitizationService desensitizationService;


    /**
     * 关注某人
     *
     * @param param 关注id
     * @return ResponseResult<Boolean>
     */

    @SaCheckLogin
    @PostMapping("/follow")
    public ResponseResult<Boolean> follow(@RequestBody FollowParam param) {

        String loginId = (String) StpUtil.getLoginId();

        if (loginId.equals(param.getFollowId())){
            return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN,"不能关注自己噢",false);
        }


        boolean followRes = relationFollowService.follow(loginId, param.getFollowId());


        return followRes ?
                new ResponseResult<>(HttpStatus.HTTP_OK, "关注成功", true)
                :
                new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR, "关注失败", false);

    }

    @SaCheckLogin
    @PostMapping("/unfollow")
    public ResponseResult<Boolean> unfollow(@RequestBody FollowParam param){

        String loginId = (String) StpUtil.getLoginId();

        boolean unfollowRes = relationFollowService.unfollow(loginId, param.getFollowId());

        return unfollowRes?
                new ResponseResult<>(HttpStatus.HTTP_OK,"取关成功",true)
                :
                new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"取关失败",false);

    }


    /**
     * 检查是否关注
     *
     * @param followId 查询id
     * @return ResponseResult<Boolean>
     */

    @SaCheckLogin
    @GetMapping("/check_follow")
    public ResponseResult<Boolean> checkFollow(@RequestParam String followId) {

        String loginId = (String) StpUtil.getLoginId();

        //查询是否有关注记录
        RelationFollow relationFollow = relationFollowService.getOne(
                new LambdaQueryWrapper<RelationFollow>()
                        .eq(RelationFollow::getUserId, loginId)
                        .eq(RelationFollow::getFollowId, followId)
        );


        return relationFollow != null ?
                new ResponseResult<>(HttpStatus.HTTP_OK, "已关注", true)
                :
                new ResponseResult<>(HttpStatus.HTTP_OK, "未关注", false);

    }




    @SaCheckLogin
    @GetMapping("/get_follows")
    public ResponseResult<List<UserVo>> getFollows(String userId){


        String loginId = (String) StpUtil.getLoginId();

        if (!loginId.equals(userId)){
            UserSettings settings = userSettingsService.getOne(new LambdaQueryWrapper<UserSettings>().eq(UserSettings::getUserId, userId));
            if (settings.getFollowPublic().equals(0)){
                return new ResponseResult<>(HttpStatus.HTTP_NO_CONTENT,"不公开",null);
            }
        }


        List<MUser> followList = relationFollowService.getFollowList(userId);


        List<UserVo> res = desensitizationService.desensitizeUserInfo(followList);

        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取成功",res);
    }



    @SaCheckLogin
    @GetMapping("/get_fans")
    public ResponseResult<List<UserVo>> getFans(@RequestParam String userId){

        String loginId = (String) StpUtil.getLoginId();
        if (!loginId.equals(userId)){
            UserSettings settings = userSettingsService.getOne(new LambdaQueryWrapper<UserSettings>().eq(UserSettings::getUserId, userId));
            if (settings.getFollowerPublic().equals(0)){
                return new ResponseResult<>(HttpStatus.HTTP_NO_CONTENT,"不公开",null);
            }
        }

        List<MUser> fans = relationFollowService.getFollowerList(userId);

        List<UserVo> res = desensitizationService.desensitizeUserInfo(fans);


        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取成功",res);

    }



}
