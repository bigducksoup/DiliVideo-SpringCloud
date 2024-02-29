package com.ducksoup.dilivideotranscoding.service;

import com.ducksoup.dilivideotranscoding.function.FileCombiner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Service
public class MyFileCombineService implements FileCombineService {

    public File combine(List<File> files,FileCombiner combiner) throws IOException {
        return combiner.combine(files);
    }

}
