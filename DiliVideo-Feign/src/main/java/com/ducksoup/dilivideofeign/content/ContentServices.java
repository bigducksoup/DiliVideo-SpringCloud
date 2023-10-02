package com.ducksoup.dilivideofeign.content;


import com.ducksoup.dilivideoentity.content.FileInfoUpdateParam;
import com.ducksoup.dilivideoentity.content.Videoinfo;
import com.ducksoup.dilivideoentity.dto.FileDeleteDTO;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import com.ducksoup.dilivideoentity.dto.FileInfo;
import com.ducksoup.dilivideoentity.dto.FileTransmissionInfo;
import com.ducksoup.dilivideoentity.dto.FileUploadDTO;
import com.ducksoup.dilivideofeign.Inteceptor.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient(value = "DiliVideo-Content",configuration = FeignInterceptor.class)
public interface ContentServices {

    @PostMapping(path = "/video/upload" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     Boolean transmission(FileTransmissionInfo fileTransmissionInfo);


    @GetMapping(path = "/feign/video_info/getById")
     ResponseResult<Videoinfo> getVideoInfoById(@RequestParam(value = "videoInfoId") String videoInfoId);

    @PostMapping(path = "/feign/upload/file" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseResult<FileInfo> uploadFile(FileUploadDTO fileUploadDTO);

    @PostMapping(path = "/feign/delete/file")
    void deleteFile(@RequestBody List<FileDeleteDTO> fileDeleteDTOS);

    @PostMapping(path = "/feign/video_info/update_file_info")
    boolean setVideoInfoStatus(@RequestBody FileInfoUpdateParam fileStatusUpdateParam);

}
