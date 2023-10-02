package com.ducksoup.dilivideoentity.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoUpdateParam {

    private String videoInfoId;

    private String videoFileId;

    private Integer status;

}
