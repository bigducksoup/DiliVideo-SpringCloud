package com.ducksoup.dilivideocontent.feign;

import com.ducksoup.dilivideocontent.mainservices.MinIO.DeleteService;
import com.ducksoup.dilivideoentity.dto.FileDeleteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feign/delete")
public class FeignFileDeleteController {

    @Autowired
    private DeleteService deleteService;


    @PostMapping("/file")
    public void deleteFile(@RequestBody List<FileDeleteDTO> fileDeleteDTO){

        fileDeleteDTO.forEach(item->{
            deleteService.deleteObj(item.getBucket(), item.getPath());
        });

    }

}
