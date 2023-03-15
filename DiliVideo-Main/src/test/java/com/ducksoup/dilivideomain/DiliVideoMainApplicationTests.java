package com.ducksoup.dilivideomain;

import com.ducksoup.dilivideoentity.dto.FileTransmissionInfo;
import com.ducksoup.dilivideofeign.Content.ContentServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@SpringBootTest
class DiliVideoMainApplicationTests {


    @Autowired
    private ContentServices contentService;

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

}
