package com.ducksoup.dilivideomain.controller.params;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ForwardVideoParams {

    private String videoInfoId;

    private String content;

    private List<MultipartFile> files;

}
