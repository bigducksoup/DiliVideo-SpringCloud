package com.ducksoup.dilivideocontent.mainservices.MinIOImpl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.DigestUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideocontent.Entity.Videofile;
import com.ducksoup.dilivideocontent.service.VideofileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class UploadService implements com.ducksoup.dilivideocontent.mainservices.MinIO.UploadService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private VideofileService videofileService;

    @Override
    public void test() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        String img = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder().bucket("img").object("jhk-1648108036077.jpg").method(Method.GET).expiry(1, TimeUnit.MINUTES).build()
        );
        GetObjectResponse img1 = minioClient.getObject(GetObjectArgs.builder()
                .bucket("img")
                .object("jhk-1648108036077.jpg")

                .build());
        String s = DigestUtil.md5Hex(img1);
        System.out.println(s);
        System.out.println(img);
    }





    @Override
    public String uploadVideo(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String md5= DigestUtil.md5Hex(file.getInputStream());
        Videofile videofileindatabase = videofileService.getOne(new LambdaQueryWrapper<Videofile>().eq(Videofile::getMd5, md5));

        if (videofileindatabase!=null){
            return null;
        }

        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");

        String uuid = UUID.randomUUID().toString();

        //filename
        String filename = uuid+"."+split[1];

        String folder = md5.substring(0,2)+"/"+md5.substring(2,4)+"/";

        //path
        String obj = folder + filename;

        ObjectWriteResponse video = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("video")
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .object(obj)
                        .build()
        );
        DateTime now = DateTime.now();
        Videofile videofile = new Videofile();
        videofile.setOriginName(file.getOriginalFilename());
        videofile.setMd5(md5);
        videofile.setBucketid("1");
        videofile.setFullpath("http://127.0.0.1:9000/"+obj);
        videofile.setId(uuid);
        videofile.setSize(file.getSize());
        videofile.setState(1);
        videofile.setUploadTime(now);
        videofile.setPath(obj);

        boolean save = videofileService.save(videofile);


        return save? uuid:null;
    }


}
