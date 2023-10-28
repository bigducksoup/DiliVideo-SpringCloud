package com.ducksoup.dilivideotranscoding.services.filestorage;

public interface FileInfoService {

    boolean checkFileExist(String fileName,String path,String bucket);
}
