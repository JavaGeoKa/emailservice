package com.geo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


//@Service
public class EmailSendService {

    @Autowired
    private JavaMailSender emailSender;
    String to = "sd_support_test@chelpipegroup.com";
    String subject = "test";
    String text = "test";


    @Scheduled(fixedDelay = 5000)
    public void sendSimpleMessage() {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sd_support_test@chelpipegroup.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
        System.out.println("ok");

    }

}
