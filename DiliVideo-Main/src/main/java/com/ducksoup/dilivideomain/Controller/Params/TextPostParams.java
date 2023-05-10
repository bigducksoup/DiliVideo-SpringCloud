package com.ducksoup.dilivideomain.Controller.Params;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class TextPostParams {

    private String content;

    private String topicId;

    private List<MultipartFile> files;



}
