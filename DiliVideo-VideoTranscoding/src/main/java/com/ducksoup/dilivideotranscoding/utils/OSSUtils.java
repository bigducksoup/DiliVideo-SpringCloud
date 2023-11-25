package com.ducksoup.dilivideotranscoding.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class OSSUtils {


    @Value(value = "${minio.endpoint}")
    private String minIOEndPoint;



    public String makeUrl(String bucket,String path){

        // fullPath ->  http://127.0.0.1:9000/img/68/d0/97343295-6489-4a4d-9f07-8b66095c8a39.jpg
        // path     ->  68/d0/97343295-6489-4a4d-9f07-8b66095c8a39.jpg
        // bucket   ->  img
        // minIOEndPoint -> http://127.0.0.1:9000

        return minIOEndPoint + "/" + bucket + "/" + path;


    }


    public String getMinIOEndPoint() {
        return minIOEndPoint;
    }


    public String generatePath(String md5,String fileName){

        String dir1 = md5.substring(0, 2);
        String dir2 = md5.substring(2, 4);

        return dir1 + "/" + dir2 + "/" + fileName;
    }

}
