package com.ducksoup.dilivideoauth.mainServices;

public interface MailSenderService {

    void sendVerifyCodeMail(String to,String code);

}
