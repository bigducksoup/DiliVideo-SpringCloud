package com.ducksoup.dilivideocontent.mainservices.MinIO;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideocontent.entity.VideoChunk;
import com.ducksoup.dilivideocontent.entity.Videofile;
import com.ducksoup.dilivideocontent.dto.FileSavedInfo;
import com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl.DownLoadFromMinIOService;
import com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl.UploadServiceForFile;
import com.ducksoup.dilivideocontent.service.VideoChunkService;
import com.ducksoup.dilivideocontent.utils.OSSUtils;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideoentity.Constant.CONSTANT_MinIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileCombineServiceImpl implements FileCombineService{


    @Autowired
    private DownLoadFromMinIOService downLoadFromMinIOService;


    @Autowired
    private UploadServiceForFile uploadServiceForFile;

    @Autowired
    private VideoChunkService videoChunkService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OSSUtils ossUtils;

    @Override
    public File combineChunks(Set<VideoChunk> chunkSet,String fileName)  {

        List<VideoChunk> chunkList = new ArrayList<>(chunkSet);
        chunkList.sort(Comparator.comparing(VideoChunk::getChunkIndex));

        log.info("共"+chunkList.size()+"片文件需合并");

        List<File> chunks = new ArrayList<>();
        chunkList.forEach(item->{
            try {
                File file = downLoadFromMinIOService.downLoadObject(item.getChunkBucket(), item.getChunkPath(), item.getOriginalName());
                chunks.add(file);
            } catch (Exception e) {
                chunks.forEach(chunk->{
                    chunk.delete();
                });
                throw new RuntimeException(e);
            }
        });
        File mergedFile = null;
        try {
            //创建临时文件
            mergedFile = File.createTempFile(FileUtil.getPrefix(fileName),"."+FileUtil.getSuffix(fileName));
            // 创建输出流
            OutputStream outputStream = Files.newOutputStream(mergedFile.toPath());
            // 逐个读取输入文件并写入输出文件
            for (File f : chunks){
                FileInputStream inputStream = new FileInputStream(f);
                int data = inputStream.read();
                while (data != -1) {
                    outputStream.write(data);
                    data = inputStream.read();
                }
                inputStream.close();
                f.delete();
            }
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<String> list = chunkSet.stream().map(VideoChunk::getId).collect(Collectors.toList());
        videoChunkService.update(new LambdaUpdateWrapper<VideoChunk>().set(VideoChunk::getUsed,1).in(VideoChunk::getId,list));
        return mergedFile;
    }

    @Async
    @Override
    public void combineVideoChunks(Set<VideoChunk> chunkSet,String fileName,String code)  {
        File file = combineChunks(chunkSet,fileName);

        log.info(file.getName()+"合成完毕+code="+code);

        try {
            FileSavedInfo savedInfo = uploadServiceForFile.uploadFile(file, CONSTANT_MinIO.VIDEO_BUCTET, DigestUtil.md5Hex(file));
            Videofile videofile = new Videofile();
            videofile.setId(UUID.randomUUID().toString());
            videofile.setOriginName(fileName);
            videofile.setUniqueName(savedInfo.getFileName());
            videofile.setPath(savedInfo.getPath());
            videofile.setBucket(CONSTANT_MinIO.VIDEO_BUCTET);
            videofile.setFullpath(ossUtils.makeUrl(CONSTANT_MinIO.VIDEO_BUCTET,savedInfo.getPath()));
            videofile.setUploadTime(DateTime.now());
            videofile.setSize(FileUtil.size(file));
            videofile.setState(0);
            videofile.setFullpath(videofile.getFullpath());
            videofile.setMd5(DigestUtil.md5Hex(file));
            redisUtil.set(code,videofile,1L, TimeUnit.DAYS);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        file.delete();
    }
}
