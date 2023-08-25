package com.ducksoup.dilivideocontent.controller.Feign;


import cn.hutool.core.date.DateTime;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideocontent.dto.FileSavedInfo;
import com.ducksoup.dilivideocontent.mainservices.MinIO.UploadService;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideoentity.dto.FileInfo;
import com.ducksoup.dilivideoentity.dto.FileUploadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping("/feign/upload")
public class FeignUploadController {

    @Autowired
    private UploadService uploadService;

    @Value("${minio.endpoint}")
    private String minIOEndPoint;

    @PostMapping("/file")
    public ResponseResult<FileInfo> uploadFile(FileUploadDTO fileUploadDTO){

        try {

            MultipartFile file = fileUploadDTO.getFile();
            String md5Hex = DigestUtil.md5Hex(file.getInputStream());
            FileSavedInfo fileSavedInfo = uploadService.uploadFile(file, fileUploadDTO.getBucketName(), md5Hex);
            String url = minIOEndPoint+"/"+ fileUploadDTO.getBucketName()+"/"+ fileSavedInfo.getPath();

            FileInfo fileInfo = new FileInfo();
            fileInfo.setOriginalName(file.getOriginalFilename());
            fileInfo.setPath(fileSavedInfo.getPath());
            fileInfo.setBucket(fileUploadDTO.getBucketName());
            fileInfo.setUploadTime(DateTime.now());
            fileInfo.setSize(fileInfo.getSize());
            fileInfo.setFullpath(url);
            fileInfo.setMd5(md5Hex);

            return new ResponseResult<>(HttpStatus.HTTP_OK,"上传成功",fileInfo);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,"上传失败+"+e.getMessage());
        }

    }

}
