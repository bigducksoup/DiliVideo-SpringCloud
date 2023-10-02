package com.ducksoup.dilivideotranscoding;

import com.ducksoup.dilivideoentity.content.FileInfoUpdateParam;
import com.ducksoup.dilivideofeign.auth.AuthServices;
import com.ducksoup.dilivideofeign.content.ContentServices;
import com.ducksoup.dilivideotranscoding.mq.provider.VideoPostAddProvider;
import com.ducksoup.dilivideotranscoding.services.FFMPEGService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiliVideoVideoEncodeApplicationTests {


    @Autowired
    private FFMPEGService ffmpegService;


    @Autowired
    private VideoPostAddProvider postAddProvider;




    @Test
    void contextLoads() throws Exception {

        /**
         * Videofile(id=054041dd-e8ea-4794-abd5-c592e74d1c30,
         * originName=image_picker_3D4CBA59-8121-4CA6-8109-9CA64F8A4F8F-7038-00000052D313A522trim.0BB60119-6BCD-46CC-8D90-49D41496D15A.MOV,
         * uniqueName=6f6ca53c-479f-4fca-a9ce-083c57bda65a.MOV,
         * path=b7/50/6f6ca53c-479f-4fca-a9ce-083c57bda65a.MOV,
         * bucket=video, uploadTime=Mon Oct 02 21:41:00 CST 2023,
         * videoinfoId=fa23832c-8d21-4e50-865a-1806b07f95f8,
         * size=11265567, state=0, fullpath=http://127.0.0.1:9000/video/b7/50/6f6ca53c-479f-4fca-a9ce-083c57bda65a.MOV, md5=b75013aa4a8c9851ca859160896ad90c)
         */

        // fa23832c-8d21-4e50-865a-1806b07f95f8
        // fa23832c-8d21-4e50-865a-1806b07f95f8

        //




        try {
//            authServices.getUserInfo("1");
            postAddProvider.notifyVideoPostAdd("fa23832c-8d21-4e50-865a-1806b07f95f8");

        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

}
