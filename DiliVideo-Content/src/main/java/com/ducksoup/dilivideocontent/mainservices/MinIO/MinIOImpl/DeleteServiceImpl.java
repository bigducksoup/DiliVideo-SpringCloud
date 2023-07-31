package com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl;

import com.ducksoup.dilivideocontent.mainservices.MinIO.DeleteService;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideofileService;
import com.ducksoup.dilivideocontent.service.VideoinfoService;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class DeleteServiceImpl implements DeleteService {


    @Autowired
    private MinioClient minioClient;

    @Autowired
    private VideoinfoService videoinfoService;

    @Autowired
    private VideofileService videofileService;

    @Autowired
    private CoverService coverService;




    /**
     *
     * @param obj 文件相对于桶的路径
     * @return boolean
     */

    public boolean deleteObj(String obj,String bucket){
        //删除文件
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(obj)
                    .build());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;

    }


}
