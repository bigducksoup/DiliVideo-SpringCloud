package com.ducksoup.dilivideobarrage.Controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideobarrage.Controller.params.BarrageParams;
import com.ducksoup.dilivideobarrage.Entity.BaBullet;
import com.ducksoup.dilivideobarrage.service.BaBulletService;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/barrage")
public class BarrageController {

    @Autowired
    private BaBulletService baBulletService;

    /**
     * 保存弹幕到数据库
     * @param barrageParams 发送的弹幕信息
     * @return ResponseResult
     */
    @PostMapping("/send")
    public ResponseResult<Void> sendBarrage(@RequestBody BarrageParams barrageParams) {

        String loginId = (String) StpUtil.getLoginId();

        BaBullet baBullet = new BaBullet();

        baBullet.setId(UUID.randomUUID().toString());
        baBullet.setContent(barrageParams.getContent());
        baBullet.setColor(barrageParams.getColor());
        baBullet.setAppearTimeSesonds(barrageParams.getSeconds());
        baBullet.setCreateTime(DateTime.now());
        baBullet.setVideoinfoId(barrageParams.getVideoInfoId());
        baBullet.setUserId(loginId);
        baBullet.setStatus(1);
        baBullet.setIfmid(barrageParams.getIfMid());

        boolean save = baBulletService.save(baBullet);

        if (save){
            return new ResponseResult<>(HttpStatus.HTTP_OK,"发送成功");
        }

        return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"发送失败");
    }


}
