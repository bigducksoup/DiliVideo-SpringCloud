package com.ducksoup.dilivideotranscoding.services.filecombine;


import java.io.File;
import java.util.List;

public interface FileCombineService {


    /**
     *
     * @param files 分片文件
     * @param suffix 要合并的类型
     * @return  合并后的文件
     * @throws Exception 合并失败
     */
    File combine(List<File> files,String suffix) throws Exception;


}
