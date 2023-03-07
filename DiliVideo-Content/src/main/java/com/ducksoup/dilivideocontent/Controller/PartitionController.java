package com.ducksoup.dilivideocontent.Controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideocontent.Entity.Partition;
import com.ducksoup.dilivideocontent.service.PartitionService;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/partition")
public class PartitionController {

    @Autowired
    private PartitionService partitionService;

    @SaCheckLogin
    @GetMapping("/getall")
    public ResponseResult<List<Partition>> getAll(){
        List<Partition> partitionList = partitionService.list();
        return new ResponseResult<>(HttpStatus.HTTP_OK,"获取成功",partitionList);
    }

}
