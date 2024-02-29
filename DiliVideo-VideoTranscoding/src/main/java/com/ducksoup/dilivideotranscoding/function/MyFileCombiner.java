package com.ducksoup.dilivideotranscoding.function;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * MyFileCombiner 实现FileComber
 * @see FileCombiner
 */
@AllArgsConstructor
public class MyFileCombiner implements FileCombiner{

    //合并后文件前缀
    private String prefix;

    //合并后文件后缀
    private String suffix;

    /**
     * combine 合并文件
     * @param  files List<File> files 必须为排序好的分块文件
     * @return file File 合并好的文件
     * @throws IOException IOException
     */
    @Override
    public File combine(List<File> files) throws IOException {


        File tempFile = File.createTempFile(prefix, suffix);
        FileChannel channel = new FileOutputStream(tempFile).getChannel();

        for (File file : files) {
            FileChannel chunkChannel = new FileInputStream(file).getChannel();
            channel.transferFrom(chunkChannel,channel.size(),chunkChannel.size());
            chunkChannel.close();
        }
        channel.close();
        return tempFile;
    }
}
