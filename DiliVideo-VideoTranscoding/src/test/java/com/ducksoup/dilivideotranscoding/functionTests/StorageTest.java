package com.ducksoup.dilivideotranscoding.functionTests;

import com.ducksoup.dilivideotranscoding.function.MinIODownLoader;
import com.ducksoup.dilivideotranscoding.function.MinIOUpLoader;
import com.ducksoup.dilivideotranscoding.function.UpLoader;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@SpringBootTest
public class StorageTest {


    @Resource
    private MinioClient minioClient;

    @Test
    public void TestMinIODownload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // img/8b4914ac-db7a-4a14-a73a-5f6a29e08686/3e58118e-592b-4835-803f-f41113a08f631662604316171465441.png

        MinIODownLoader downLoader = MinIODownLoader.builder()
                .bucket("img")
                .object("8b4914ac-db7a-4a14-a73a-5f6a29e08686/3e58118e-592b-4835-803f-f41113a08f631662604316171465441.png")
                .minioClient(minioClient)
                .build();


        File file = downLoader.download();

        System.out.println(file.getAbsolutePath());
    }


    @Test
    public void TestMinIOUpload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        UpLoader upLoader = MinIOUpLoader.builder()
                .bucket("video")
                .object("test/upload/test.mp4")
                .minioClient(minioClient)
                .build();


        upLoader.upload(new File("/Users/meichuankutou/Downloads/视频素材/1.mp4"));

    }

}
