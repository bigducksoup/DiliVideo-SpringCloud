package com.ducksoup.dilivideocontent.Controller.Params;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoInfoForm {

    private String title;

    //分区Id
    private String partitionId;

    //是否原创
    private boolean iforiginal;

    private String description;

    //封面文件
    private MultipartFile file;

    private String authorId;

    private String code;

}
