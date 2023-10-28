package com.ducksoup.dilivideotranscoding.services.filestorage;

import java.io.File;

public interface FileDownloadService {



    File download(String fileName,String path,String bucket) throws Exception;

}
