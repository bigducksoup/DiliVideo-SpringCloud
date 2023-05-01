package com.ducksoup.dilivideoentity.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadDTO {


    private MultipartFile file;

    private String bucketName;



}
