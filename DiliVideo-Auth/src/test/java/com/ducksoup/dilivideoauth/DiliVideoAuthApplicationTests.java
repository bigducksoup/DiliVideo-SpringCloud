package com.ducksoup.dilivideoauth;

import com.ducksoup.dilivideoauth.mainServices.Impl.QQmailSenderServiceImpl;
import com.ducksoup.dilivideoauth.mainServices.MailSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiliVideoAuthApplicationTests {


    @Autowired
    private QQmailSenderServiceImpl mailSenderService;

    @Test
    void contextLoads() {

        mailSenderService.sendVerifyCodeMail("969690525@qq.com","213444");


    }

}
