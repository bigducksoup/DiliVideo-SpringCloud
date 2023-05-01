package com.ducksoup.dilivideoauth;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import com.ducksoup.dilivideoauth.mainServices.Impl.QQmailSenderServiceImpl;
import com.ducksoup.dilivideoauth.mainServices.MailSenderService;
import com.ducksoup.dilivideoentity.dto.FileUploadDTO;
import com.ducksoup.dilivideofeign.Content.ContentServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
class DiliVideoAuthApplicationTests {


    @Autowired
    private QQmailSenderServiceImpl mailSenderService;


    @Autowired
    private ContentServices contentServices;

    @Test
    void contextLoads() {

//        mailSenderService.sendVerifyCodeMail("969690525@qq.com","213444");
        MockMultipartFile mockMultipartFile = new MockMultipartFile( "test.txt",new byte[1]);

        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFile(mockMultipartFile);
        fileUploadDTO.setBucketName("avatar");

        ResponseResult<String> stringResponseResult = contentServices.uploadFile(fileUploadDTO);

        System.out.println(stringResponseResult);

    }

}
