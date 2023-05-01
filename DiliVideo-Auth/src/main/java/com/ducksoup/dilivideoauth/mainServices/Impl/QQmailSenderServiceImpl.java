package com.ducksoup.dilivideoauth.mainServices.Impl;

import com.ducksoup.dilivideoauth.mainServices.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class QQmailSenderServiceImpl implements MailSenderService {


    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    private String msg = "欢迎使用diliVideo，您的验证码为：";

    @Override
    public void sendVerifyCodeMail(String to, String code) throws MailException {

        System.out.println(from);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("diliVideo邮箱验证");
        message.setText(msg+code);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new MailSendException("发送失败");
        }


    }
}
