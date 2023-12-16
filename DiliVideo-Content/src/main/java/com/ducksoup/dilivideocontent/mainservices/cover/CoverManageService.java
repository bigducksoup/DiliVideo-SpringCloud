package com.ducksoup.dilivideocontent.mainservices.cover;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MinIO;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class CoverManageService {

    private final MinioClient minioClient;



    public String getCoverUploadUrl(String missionId, String typeSuffix) throws Exception{

        String uploadUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .expiry(10, TimeUnit.MINUTES)
                .bucket(CONSTANT_MinIO.COVER_BUCKET)
                .method(Method.PUT)
                .object(missionId + "/" + "custom" +"/" + missionId+ "." + typeSuffix)
                .build());

        return  uploadUrl;
    }


}
