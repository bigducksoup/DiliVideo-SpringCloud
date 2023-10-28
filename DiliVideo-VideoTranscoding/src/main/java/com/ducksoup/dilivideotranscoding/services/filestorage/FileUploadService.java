package com.ducksoup.dilivideotranscoding.services.filestorage;

import java.io.File;

public interface FileUploadService {

    boolean upload(File file,String bucket,String path) throws Exception;

}
