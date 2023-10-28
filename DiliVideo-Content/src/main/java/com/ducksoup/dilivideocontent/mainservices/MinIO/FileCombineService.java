package com.ducksoup.dilivideocontent.mainservices.MinIO;

import com.ducksoup.dilivideocontent.entity.VideoChunk;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface FileCombineService {

    File combineChunks(Set<VideoChunk> chunkSet,String fileName) throws InterruptedException;

    void combineVideoChunks(Set<VideoChunk> chunkSet,String fileName,String code);

    File mergeFile(List<File> chunkFiles,String fileName) throws IOException;

}
