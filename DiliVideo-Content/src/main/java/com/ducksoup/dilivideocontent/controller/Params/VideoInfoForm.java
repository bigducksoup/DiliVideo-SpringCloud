package com.ducksoup.dilivideocontent.controller.Params;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class VideoInfoForm {

    @NotNull
    private String title;

    @NotNull
    //分区Id
    private String partitionId;

    @NotNull
    //是否原创
    private boolean iforiginal;

    @NotNull
    private String description;

    @NotNull
    //封面文件
    private MultipartFile file;

    @NotNull
    private String authorId;

    @NotNull
    private String code;

    @NotNull
    private List<String> tagIds;

}
