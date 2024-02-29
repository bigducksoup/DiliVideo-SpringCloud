package com.ducksoup.dilivideotranscoding.service.compisition;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.thread.ThreadUtil;
import com.ducksoup.dilivideotranscoding.controller.param.MinIORelated.MinIODownLoadMeta;
import com.ducksoup.dilivideotranscoding.controller.param.MinIORelated.MinIOSourceFileCombineParam;
import com.ducksoup.dilivideotranscoding.controller.param.MinIORelated.MinIOSourceTranscodeParam;
import com.ducksoup.dilivideotranscoding.controller.param.MinIORelated.QualityAndMinIOUploadMeta;
import com.ducksoup.dilivideotranscoding.entity.ConstantContext;
import com.ducksoup.dilivideotranscoding.entity.HandleResponse;
import com.ducksoup.dilivideotranscoding.entity.VideoFileInfo;
import com.ducksoup.dilivideotranscoding.function.*;
import com.ducksoup.dilivideotranscoding.service.MyFileCombineService;
import com.ducksoup.dilivideotranscoding.service.ServiceContext;
import com.ducksoup.dilivideotranscoding.service.StorageService;
import com.ducksoup.dilivideotranscoding.service.VideoHandleService;
import com.ducksoup.dilivideotranscoding.service.compisition.result.MinIOSourceCombineResult;
import com.ducksoup.dilivideotranscoding.service.compisition.result.MinIOSourceTranscodeResult;
import io.minio.MinioClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
@AllArgsConstructor
public class VideoService {

    private MinioClient minioClient;

    private MyFileCombineService myFileCombineService;


    /**
     * 视频文件转码，从MinIO下载，转码后上传到MinIO
     *
     * @param param 参数
     * @return 是否成功
     * @see MinIOSourceTranscodeParam
     */
    public MinIOSourceTranscodeResult MinIOSourceTranscode(MinIOSourceTranscodeParam param) {

        // 获取转码服务
        VideoHandleService handleService = ServiceContext.videoHandleServiceMap.getOrDefault(param.getTranscodeServiceName(), ServiceContext.videoHandleServiceMap.get("FFmpegVideoHandleService"));

        // 获取存储服务
        StorageService storageService = ServiceContext.storageServiceMap.get("MinIOStorageService");

        // 结果
        MinIOSourceTranscodeResult result = new MinIOSourceTranscodeResult();


        result.setBucket(param.getMinIODownLoadMeta().getDownloadBucket());
        result.setRegion(param.getMinIODownLoadMeta().getDownloadRegion());
        result.setObject(param.getMinIODownLoadMeta().getDownloadObjectName());

        // 如果服务不存在
        if (storageService == null || handleService == null) {
            log.error("未找到对应的服务");
            result.setCode(500);
            result.setMessage("未找到对应的服务");
            return result;
        }




        // 下载参数
        MinIODownLoadMeta downLoadMeta = param.getMinIODownLoadMeta();

        // 转码参数 与 上传参数
        List<QualityAndMinIOUploadMeta> metas = param.getQualityAndMinIOUploadMetaList();

        CountDownLatch countDownLatch = new CountDownLatch(metas.size());

        // 创建下载器
        DownLoader downLoader = MinIODownLoader.builder()
                .bucket(downLoadMeta.getDownloadBucket())
                .object(downLoadMeta.getDownloadObjectName())
                .region(downLoadMeta.getDownloadRegion())
                .extraHeaders(downLoadMeta.getDownloadExtraHeaders())
                .extraQueryParams(downLoadMeta.getDownloadExtraQueryParams())
                .minioClient(minioClient)
                .build();

        File sourceFile = null;

        try {
            // 下载
            log.info("开始下载文件,{}", downLoadMeta);
            sourceFile = downLoader.download();
        } catch (Exception e) {
            log.error("下载失败"+e.getMessage(),downLoadMeta);
            result.setCode(501);
            result.setMessage("下载原文件失败");
            return result;
        }

        result.setHandleResponses(new ArrayList<>());


        // 遍历转码与下载参数
        for (QualityAndMinIOUploadMeta meta : metas) {
            File finalSourceFile = sourceFile;
            HandleResponse handleResponse = new HandleResponse();
            handleResponse.setObject(meta.getUploadObject());
            handleResponse.setRegion(meta.getUploadRegion());
            handleResponse.setBucket(meta.getUploadBucket());
            handleResponse.setFormat(meta.getFormat());
            handleResponse.setQuality(meta.getQuality());

            ThreadUtil.execAsync(() -> {
                File transcodedFile = null;
                try {
                    // 转码
                    log.info("开始转码文件,{}", meta);
                    transcodedFile = handleService.transcode(finalSourceFile,
                            ConstantContext.getVideoFormat(meta.getFormat()),
                            ConstantContext.getVideoQuality(meta.getQuality()));

                    // 创建上传器
                    UpLoader upLoader = MinIOUpLoader.builder()
                            .bucket(meta.getUploadBucket())
                            .object(meta.getUploadObject())
                            .region(meta.getUploadRegion())
                            .extraHeaders(meta.getUploadExtraHeaders())
                            .extraQueryParams(meta.getUploadExtraQueryParams())
                            .minioClient(minioClient)
                            .build();

                    // 上传
                    log.info("开始上传文件,{}", meta);
                    upLoader.upload(transcodedFile);

                    handleResponse.setOk(true);
                    handleResponse.setMessage("success");

                } catch (Exception e) {
                    log.error("处理或上传失败：" + meta, e);
                    handleResponse.setOk(false);
                    handleResponse.setMessage(e.getMessage());
                } finally {
                    if (transcodedFile != null) transcodedFile.delete();
                    result.getHandleResponses().add(handleResponse);
                    countDownLatch.countDown();
                }
            });
        }


        try {
            log.info("等待线程执行完毕,{}", Thread.currentThread().getId());
            countDownLatch.await();
            log.info("pass,{}", Thread.currentThread().getId());
        } catch (InterruptedException e) {
            if (sourceFile != null) sourceFile.delete();
            log.error("线程等待失败", e);
            result.setCode(502);
            result.setMessage("线程等待失败");
            return result;
        }
        result.setMessage("success");
        result.setCode(200);
        if (sourceFile != null) sourceFile.delete();
        return result;
    }


    private final FFmpegVideoInfoReader fFmpegVideoInfoReader;


    /**
     * 从MinIO下载分片 并合并
     * @param param MinIOSourceFileCombineParam
     * @return MinIOSourceCombineResult
     * TODO Test
     */
    public MinIOSourceCombineResult MinIOSourceCombine(MinIOSourceFileCombineParam param) {

        MinIOSourceCombineResult result = new MinIOSourceCombineResult();
        result.setUploadMeta(param.getUploadMeta());
        result.setMsg("success");

        List<File> chunks = new ArrayList<>();

        for (MinIODownLoadMeta chunkMeta : param.getChunkMetas()) {

            DownLoader downLoader = MinIODownLoader.builder()
                    .bucket(chunkMeta.getDownloadBucket())
                    .object(chunkMeta.getDownloadObjectName())
                    .region(chunkMeta.getDownloadRegion())
                    .extraHeaders(chunkMeta.getDownloadExtraHeaders())
                    .extraQueryParams(chunkMeta.getDownloadExtraQueryParams())
                    .minioClient(minioClient)
                    .build();
            try {
                log.info("开始下载文件,{}", chunkMeta);
                chunks.add(downLoader.download());
                log.info("下载成功,{}", chunkMeta);
            } catch (Exception e) {
                log.error("下载失败"+e.getMessage(),chunkMeta);
                chunks.forEach(File::delete);
                log.error("download {} failed cancel mission",chunkMeta.getDownloadBucket()+chunkMeta.getDownloadRegion());
                result.setOk(false);
                result.setMsg(e.getMessage());
                return result;
            }
        }

        File combinedFile = null;
        FileCombiner fileCombiner = new MyFileCombiner(UUID.fastUUID().toString(),".temp");
        try {
            log.info("开始合并文件,{}", param.getUploadMeta());
            combinedFile = myFileCombineService.combine(chunks, fileCombiner);
            log.info("合并成功,{}", param.getUploadMeta());
        } catch (Exception e) {
            log.error("合并失败",e);
            chunks.forEach(File::delete);
            combinedFile.delete();
            log.error("combine file failed cancel mission,{}",param.getUploadMeta().getUploadObject());
            result.setOk(false);
            result.setMsg(e.getMessage());
            return result;
        }


        VideoFileInfo info;

        try {
           info = fFmpegVideoInfoReader.read(combinedFile);
        }catch (Exception e){
            result.setOk(false);
            result.setMsg(e.getMessage());
            return result;
        }

        UpLoader upLoader = MinIOUpLoader.builder()
                .bucket(param.getUploadMeta().getUploadBucket())
                .object(param.getUploadMeta().getUploadObject())
                .region(param.getUploadMeta().getUploadRegion())
                .extraHeaders(param.getUploadMeta().getUploadExtraHeaders())
                .extraQueryParams(param.getUploadMeta().getUploadExtraQueryParams())
                .minioClient(minioClient)
                .build();
        try {
            log.info("开始上传文件,{}", param.getUploadMeta());
            upLoader.upload(combinedFile);
            log.info("上传成功,{}", param.getUploadMeta());
        } catch (Exception e) {
            log.error("上传失败",e);
            chunks.forEach(File::delete);
            combinedFile.delete();
            log.error("upload {} failed cancel mission",param.getUploadMeta().getUploadObject());
            result.setOk(false);
            result.setMsg(e.getMessage());
            return result;
        }


        result.setVideoFileInfo(info);
        result.setOk(true);

        return result;
    }

}
