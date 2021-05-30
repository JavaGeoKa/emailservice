package com.geo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
public class EmailSendService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;




    String to = "sd_support_test@chelpipegroup.com";
    String subject = "request";
    String text = "test";
    String htmlBody = "";
    Integer i = 0;



    List<String> requests = new ArrayList<>();
    @Value("${app.attachments}") String attachments;
    {
        requests.add("Сломался монитор, не показывает изображение");
        requests.add("Не выходит в интернет компьютер");
        requests.add("Зависает программаб долго грузится приложение");
    }

    @Scheduled(fixedDelay = 5000)
    public void sendMessageMethod() throws Exception {
        if (new Random().nextBoolean() == true) {
            System.out.println("no attachments");
            sendHtmlMessage();
//            sendSimpleMessage();
        }  else {
            System.out.println("attachments");
//            sendAttachmentsMessages();
        }

    }

    private void sendAttachmentsMessages() throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("sd_support_test@chelpipegroup.com");
        helper.setTo(to);
        helper.setSubject(subject + i++);
        helper.setText(requests.get(new Random().nextInt(requests.size())) + " attachments");

        Files.walk(Paths.get(attachments))
                .filter(Files::isRegularFile)
                .forEach(f -> {
                    FileSystemResource file = new FileSystemResource(f);
                    try {
                        helper.addAttachment(file.getFilename(), file);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                });
        emailSender.send(message);

    }


    public void sendSimpleMessage() {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sd_support_test@chelpipegroup.com");
        message.setTo(to);
        message.setSubject(subject + i++);
        message.setText(requests.get(new Random().nextInt(requests.size())) + "   no attachments");
        emailSender.send(message);
        System.out.println("ok");

    }


private void sendHtmlMessage() throws MessagingException {
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setFrom("sd_support_test@chelpipegroup.com");
    helper.setTo(to);
    helper.setSubject(subject + i++);
    Context thymeleafContext = new Context();
    htmlBody =  thymeleafTemplateEngine.process("template-thymeleaf.html", thymeleafContext);
    helper.setText(htmlBody, true);
    emailSender.send(message);
}
}