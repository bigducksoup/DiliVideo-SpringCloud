package com.ducksoup.dilivideotranscoding.services.filecombine.Impl;

import cn.hutool.core.lang.UUID;
import com.ducksoup.dilivideotranscoding.services.filecombine.FileCombineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;


@Slf4j
@Service
public class FileCombineServiceImpl implements FileCombineService {



    @Override
    public File combine(List<File> files,String suffix) throws Exception {



        //创建临时文件
        File tempFile = File.createTempFile(UUID.fastUUID().toString(),"."+suffix);


        try (FileChannel channel = new FileOutputStream(tempFile).getChannel()) {
            //合并文件
            for (File file : files) {
                FileChannel chunkChannel = new FileInputStream(file).getChannel();
                channel.transferFrom(chunkChannel,channel.size(),chunkChannel.size());
                chunkChannel.close();
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }


        for (File file : files) {
            file.delete();
        }


        return tempFile;
    }



}
