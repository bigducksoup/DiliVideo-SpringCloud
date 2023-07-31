package com.ducksoup.dilivideocontent.mainservices.MinIO;

import com.ducksoup.dilivideocontent.entity.VideoChunk;

import java.io.File;
import java.util.Set;

public interface FileCombineService {

    File combineChunks(Set<VideoChunk> chunkSet,String fileName);

    void combineVideoChunks(Set<VideoChunk> chunkSet,String fileName,String code);

}
