package com.rest.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public String sendEmail(String toMail,String sub,String body){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("balu.mahi1123456@gmail.com");
        message.setTo(toMail);
        message.setText(body);
        message.setSubject(sub);
        mailSender.send(message);
        return("mailsent successfully..");
    }
}
