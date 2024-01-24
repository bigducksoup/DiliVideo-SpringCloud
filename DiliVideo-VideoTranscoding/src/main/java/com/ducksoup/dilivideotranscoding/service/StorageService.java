package com.ducksoup.dilivideotranscoding.service;

import com.ducksoup.dilivideotranscoding.function.DownLoader;
import com.ducksoup.dilivideotranscoding.function.UpLoader;
import java.io.File;
public interface StorageService {

    void upload(UpLoader upLoader,File file) throws Exception;

    File download(DownLoader downLoader) throws Exception;
}
