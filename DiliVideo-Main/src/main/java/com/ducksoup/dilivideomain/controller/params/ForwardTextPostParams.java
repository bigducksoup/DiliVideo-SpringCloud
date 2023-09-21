package com.ducksoup.dilivideomain.controller.params;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ForwardTextPostParams {


    private String postId;

    private String content;

    private List<MultipartFile> files;


}
