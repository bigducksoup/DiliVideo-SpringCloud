package com.ducksoup.dilivideocontent.controller.Params;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChunkParams {

    private String code;

    private MultipartFile file;

    private Integer index;

    private Integer totalChunkCount;

    private String fileName;

}
