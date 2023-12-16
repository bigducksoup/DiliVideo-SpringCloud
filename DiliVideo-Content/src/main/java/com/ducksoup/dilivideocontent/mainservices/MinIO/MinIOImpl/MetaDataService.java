package com.ducksoup.dilivideocontent.mainservices.MinIO.MinIOImpl;

import io.minio.*;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@AllArgsConstructor
public class MetaDataService {

    private final MinioClient minioClient;


    /**
     * 获取文件夹内的文件信息
     * @param bucket bucket
     * @param dir  e.g. /abc/def
     * @param ignoreDir 是否忽略文件夹
     * @return  List<Item>
     * @see Item
     */
    public List<Item> ListItemsInDir(String bucket,String dir, boolean ignoreDir){

        //获取上传文件列表
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(dir)
                .recursive(true)
                .build());

        List<Item> items = new ArrayList<>();

        Iterator<Result<Item>> iterator = results.iterator();

        while (iterator.hasNext()){
            try {

                Item item = iterator.next().get();
                if (!item.isDir()){
                    items.add(item);
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        return items;

    }

    /**
     * 获取文件信息
     * @param bucket
     * @param obj
     * @return StatObjectResponse from MinIO
     */
    public StatObjectResponse statObject(String bucket,String obj){
        StatObjectResponse statObjectResponse = null;
        try {
            statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(obj)
                    .build());
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return statObjectResponse;
    }





}
