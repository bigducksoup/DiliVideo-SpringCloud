package com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.DigestUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ducksoup.dilivideocontent.entity.Cover;
import com.ducksoup.dilivideocontent.entity.Videofile;
import com.ducksoup.dilivideocontent.dto.FileSavedInfo;
import com.ducksoup.dilivideocontent.service.CoverService;
import com.ducksoup.dilivideocontent.service.VideofileService;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MinIO;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UploadServiceImpl implements com.ducksoup.dilivideocontent.mainservices.MinIO.UploadService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private VideofileService videofileService;

    @Autowired
    private CoverService coverService;

    @Autowired
    private RedisUtil redisUtil;

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


    @Value("${minio.endpoint}")
    private String minIOEndPoint;



    @Override
    public String uploadVideo(MultipartFile file,String code) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String md5= DigestUtil.md5Hex(file.getInputStream());
        Videofile videofileindatabase = videofileService.getOne(new LambdaQueryWrapper<Videofile>().eq(Videofile::getMd5, md5));

        if (videofileindatabase!=null){
            redisUtil.set(code,  videofileindatabase, 1L,TimeUnit.DAYS);
            return videofileindatabase.getId();
        }


        //获取后缀
        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");

        String uuid = UUID.randomUUID().toString();

        //filename
        String filename = uuid+"."+split[split.length-1];

        String folder = md5.substring(0,2)+"/"+md5.substring(2,4)+"/";

        //path
        String obj = folder + filename;

        ObjectWriteResponse video = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(CONSTANT_MinIO.VIDEO_BUCKET)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .object(obj)
                        .build()
        );
        DateTime now = DateTime.now();
        Videofile videofile = new Videofile();
        videofile.setOriginName(file.getOriginalFilename());
        videofile.setMd5(md5);
        videofile.setBucket(CONSTANT_MinIO.VIDEO_BUCKET);
        String[] objsplit = obj.split("\\.");
        String nobj = objsplit[0]+"."+"mp4";
        videofile.setFullpath(minIOEndPoint+"/"+CONSTANT_MinIO.VIDEO_BUCKET +"/"+nobj);
        videofile.setId(uuid);
        videofile.setSize(file.getSize());
        videofile.setState(1);
        videofile.setUploadTime(now);
        videofile.setPath(obj);
        videofile.setUniqueName(filename);

        log.info("视频"+videofile.getFullpath()+"上传成功");
        redisUtil.set(code,videofile,1L,TimeUnit.DAYS);


        return uuid;
    }

    @Override
    public String uploadCover(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String md5 = DigestUtil.md5Hex(file.getInputStream());

//        //寻找数据库中md5相同的cover实现秒传
//        Cover coverindatabase = coverService.getOne(new LambdaQueryWrapper<Cover>().eq(Cover::getMd5, md5));
//
//        if (coverindatabase!=null){
//            return coverindatabase.getId();
//        }



        FileSavedInfo image = uploadFile(file, CONSTANT_MinIO.COVER_BUCKET, md5);

        Cover cover = new Cover();
        String uuid = UUID.randomUUID().toString();
        cover.setId(uuid);
        cover.setFullpath( minIOEndPoint+"/"+CONSTANT_MinIO.COVER_BUCKET+"/"+image.getPath());
        cover.setBucket(CONSTANT_MinIO.COVER_BUCKET);
        cover.setSize(file.getSize());
        cover.setMd5(md5);
        cover.setUploadTime(DateTime.now());
        cover.setState(1);
        cover.setUniqueName(image.getFileName());
        cover.setOriginalName(file.getOriginalFilename());
        cover.setPath(image.getPath());

        coverService.save(cover);
        log.info("cover:"+cover.getFullpath()+"上传成功");
        return uuid;
    }

    @Override
    public FileSavedInfo uploadFile(MultipartFile file, String BucketName, String md5) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        String[] split = file.getOriginalFilename().split("\\.");
        //后缀
        String suffix = split[split.length-1];
        //id
        String uuid = UUID.randomUUID().toString();
        //文件名
        String fileName = uuid+"."+suffix;
        //文件夹（相对于bucket）
        String folder = md5.substring(0,2)+"/"+md5.substring(2,4)+"/";
        //相对于bucket的路径
        String obj = folder+fileName;

       minioClient.putObject(PutObjectArgs.builder()
                       .bucket(BucketName)
                       .stream(file.getInputStream(), file.getSize(), -1)
                       .object(obj)
               .build());

        FileSavedInfo fileSavedInfo = new FileSavedInfo();
        fileSavedInfo.setFileName(fileName);
        fileSavedInfo.setSuffix(suffix);
        fileSavedInfo.setPath(obj);
        return fileSavedInfo;
    }


}
