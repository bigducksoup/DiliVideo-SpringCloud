package com.ducksoup.dilivideocontent.Controller.Feign;


import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideocontent.dto.FileSavedInfo;
import com.ducksoup.dilivideocontent.mainservices.MinIO.UploadService;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import com.ducksoup.dilivideoentity.dto.FileUploadDTO;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;



@Slf4j
@RestController
@RequestMapping("/feign/upload")
public class FeignUploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/file")
    public ResponseResult<String> uploadFile(FileUploadDTO fileUploadDTO){


        try {
            String md5Hex = DigestUtil.md5Hex(fileUploadDTO.getFile().getInputStream());
            FileSavedInfo fileSavedInfo = uploadService.uploadFile(fileUploadDTO.getFile(), fileUploadDTO.getBucketName(), md5Hex);
            String url = "http://127.0.0.1:9000/"+ fileUploadDTO.getBucketName()+"/"+ fileSavedInfo.getPath();
            return new ResponseResult<>(HttpStatus.HTTP_OK,"上传成功",url);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"上传失败+"+e.getMessage());
        }


    }

}
