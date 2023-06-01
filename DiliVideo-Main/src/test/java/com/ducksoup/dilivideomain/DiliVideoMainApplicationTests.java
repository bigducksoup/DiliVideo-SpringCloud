package com.ducksoup.dilivideomain;

import com.ducksoup.dilivideoentity.dto.FileTransmissionInfo;
import com.ducksoup.dilivideofeign.Content.ContentServices;
import com.ducksoup.dilivideomain.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@SpringBootTest
class DiliVideoMainApplicationTests {


    @Autowired
    private ContentServices contentService;

    @Autowired
    private CommentService commentService;

    @Test
    void Test() throws IOException {
        FileTransmissionInfo fileTransmissionInfo = new FileTransmissionInfo();

        File file = new File("/Users/meichuankutou/Pictures/icons/14.png");

        System.out.println(file.getAbsolutePath());
        MockMultipartFile mockMultipartFile = new MockMultipartFile(file.getName(), Files.newInputStream(file.toPath()));


        fileTransmissionInfo.setFile(mockMultipartFile);

        fileTransmissionInfo.setOriginalFileName(file.getName());

        fileTransmissionInfo.setVideoInfoId("1");

        contentService.transmission(fileTransmissionInfo);
    }



    @Test
    void commentQueryTest(){
        String id = "8269d8d5-a0c3-42de-b942-bc44844b2fe0";
        List<String> ids = commentService.queryCommentIdsByVideoInfoIdSortByLikeCount(id, 2, 5);
        List<String> ids2 = commentService.queryCommentIdsByVideoInfoIdSortByTime(id, 1, 10);
        System.out.println(ids);
        System.out.println(ids2);
    }


}
