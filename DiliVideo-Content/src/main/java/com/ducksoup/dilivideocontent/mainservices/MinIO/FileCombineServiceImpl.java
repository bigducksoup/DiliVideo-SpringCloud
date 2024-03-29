package com.ducksoup.dilivideocontent.mainservices.MinIO;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ducksoup.dilivideocontent.entity.ChunkFile;
import com.ducksoup.dilivideocontent.entity.VideoChunk;
import com.ducksoup.dilivideocontent.entity.Videofile;
import com.ducksoup.dilivideocontent.dto.FileSavedInfo;
import com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl.DownLoadFromMinIOService;
import com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl.UploadServiceForFile;
import com.ducksoup.dilivideocontent.service.VideoChunkService;
import com.ducksoup.dilivideocontent.utils.OSSUtils;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import com.ducksoup.dilivideoentity.constant.CONSTANT_MinIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
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


    @Autowired
    private ThreadPoolTaskExecutor IOThreadPool;

    @Override
    public File combineChunks(Set<VideoChunk> chunkSet,String fileName) {

        List<VideoChunk> chunkList = new CopyOnWriteArrayList<>(chunkSet);


        log.info("共"+chunkList.size()+"片文件需合并");

        List<ChunkFile> chunks = new CopyOnWriteArrayList<>();

        CountDownLatch countDownLatch = new CountDownLatch(chunkList.size());

        for (VideoChunk item : chunkList) {
            IOThreadPool.execute(() -> {
                File file = null;
                try {
                    file = downLoadFromMinIOService.downLoadObject(item.getChunkBucket(), item.getChunkPath(), item.getOriginalName());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }finally {
                    countDownLatch.countDown();
                }
                chunks.add(new ChunkFile(file,item.getChunkIndex()));
            });
        }



        try {
            countDownLatch.await();



            if (chunkList.size()!=chunks.size()){
                for (ChunkFile chunk : chunks) {
                    chunk.getChunk().delete();
                }
                throw new RuntimeException("分片缺失");
            }

            chunks.sort(Comparator.comparing(ChunkFile::getIndex));

            List<File> files = chunks.stream().map(ChunkFile::getChunk).collect(Collectors.toList());

            File mergedFile = mergeFile(files,fileName);


            List<String> list = chunkSet.stream().map(VideoChunk::getId).collect(Collectors.toList());
            videoChunkService.update(new LambdaUpdateWrapper<VideoChunk>().set(VideoChunk::getUsed,1).in(VideoChunk::getId,list));
            return mergedFile;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    public File mergeFile(List<File> chunkFiles,String fileName) throws IOException {

        //创建临时文件
        File mergedFile = File.createTempFile(FileUtil.getPrefix(fileName),"."+FileUtil.getSuffix(fileName));


        // 创建输出流
        OutputStream outputStream = Files.newOutputStream(mergedFile.toPath());


        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        // 逐个读取输入文件并写入输出文件
        for (File f : chunkFiles){
            BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(f.toPath()));

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read() )!= -1) {
                bufferedOutputStream.write(buffer,0,bytesRead);
            }
            inputStream.close();
            f.delete();
        }
        bufferedOutputStream.close();
        outputStream.close();

        return mergedFile;
    }


    @Override
    public void combineVideoChunks(Set<VideoChunk> chunkSet,String fileName,String code)  {
        File file = combineChunks(chunkSet,fileName);

        log.info(file.getName()+"合成完毕+code="+code);

        try {
            FileSavedInfo savedInfo = uploadServiceForFile.uploadFile(file, CONSTANT_MinIO.VIDEO_BUCKET, DigestUtil.md5Hex(file));

            Videofile videofile = new Videofile();
            videofile.setId(UUID.randomUUID().toString());
            videofile.setOriginName(fileName);
            videofile.setUniqueName(savedInfo.getFileName());
            videofile.setPath(savedInfo.getPath());
            videofile.setBucket(CONSTANT_MinIO.VIDEO_BUCKET);
            videofile.setFullpath(ossUtils.makeUrl(CONSTANT_MinIO.VIDEO_BUCKET,savedInfo.getPath()));
            videofile.setUploadTime(DateTime.now());
            videofile.setSize(FileUtil.size(file));
            videofile.setState(0);
            videofile.setMd5(DigestUtil.md5Hex(file));
            redisUtil.set(code,videofile,1L, TimeUnit.DAYS);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        file.delete();
    }




}
