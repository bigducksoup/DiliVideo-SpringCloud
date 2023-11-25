package com.ducksoup.dilivideotranscoding.services.filestorage.Impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileDownloadService;
import com.ducksoup.dilivideotranscoding.services.filestorage.FileInfoService;
import io.minio.DownloadObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;


@Slf4j
public class MinioFileDownloadService implements FileDownloadService {

    @Autowired
    private MinioClient minioClient;


    @Autowired
    private FileInfoService minioFileInfoService;

    @Override
    public File download(String fileName, String path, String bucket) throws Exception {

        boolean fileExist = minioFileInfoService.checkFileExist(fileName, path, bucket);

        if (!fileExist){
            log.error("{} not exist,bucket:{},path:{}",fileName,bucket,path);
            throw new RuntimeException("file not found");
        }


        GetObjectArgs downloadObjectArgs = GetObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build();

        GetObjectResponse inputStream = minioClient.getObject(downloadObjectArgs);

        String extName = "." + FileNameUtil.extName(fileName);
        File tempFile = File.createTempFile(fileName, extName);

        FileUtil.writeFromStream(inputStream,tempFile);

        return tempFile;
    }



    @Override
    public List<File> multiThreadsDownLoad(List<String> objectNames, String bucket) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(objectNames.size());

        List<File> files = new CopyOnWriteArrayList<>();


        for (String objectName : objectNames) {
            new Thread(()->{
                try {
                    //获取输入流
                    InputStream inputStream =  minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(objectName).build());
                    //创建文件
                    File file  = new File(objectName);
                    //将输入流写入文件
                    FileUtils.copyInputStreamToFile(inputStream,file);
                    inputStream.close();
                    //添加到结果
                    files.add(file);
                }catch (Exception e){
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }finally {
                    countDownLatch.countDown();
                }
            }).start();
        }

        countDownLatch.await();
        return files;
    }



}
