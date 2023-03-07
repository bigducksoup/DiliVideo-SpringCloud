package com.ducksoup.dilivideofeign.BarrageServices;


import com.ducksoup.dilivideoentity.dto.FileTransmissionInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@FeignClient(value = "DiliVideo-Content")
public interface ContentService {

    @PostMapping(path = "/video/upload" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Boolean transmission(FileTransmissionInfo fileTransmissionInfo);

}
