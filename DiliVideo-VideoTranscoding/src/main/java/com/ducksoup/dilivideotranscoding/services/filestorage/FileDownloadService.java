package com.ducksoup.dilivideotranscoding.services.filestorage;

import java.io.File;
import java.util.List;

public interface FileDownloadService {



    File download(String fileName,String path,String bucket) throws Exception;


    List<File> multiThreadsDownLoad(List<String> objectNames, String bucket) throws InterruptedException;
}
