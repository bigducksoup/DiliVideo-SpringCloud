package com.ducksoup.dilivideoentity.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileTransmissionInfo {


    private MultipartFile file;

    private String originalFileName;

    private String videoInfoId;

}
