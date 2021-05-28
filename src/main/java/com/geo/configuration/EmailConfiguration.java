package com.geo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.annotation.PostConstruct;
import javax.mail.*;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class EmailConfiguration {


    @Bean
    public JavaMailSender getJavaMailSender(
        @Value("${app.smtp.host}") String host,
        @Value("${app.smtp.port}") Integer port,
        @Value("${app.smtp.username}") String username,
        @Value("${app.smtp.password}") String password)  {


        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.socketFactory.fallback", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
//        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.debug", "true");
        props.put("mail.smtp.connectiontimeout", 5000);
        props.put("mail.smtp.timeout", 5000);
        props.put("mail.smtp.writetimeout", 5000);
//        props.put("mail.smtp.ssl.checkserveridentity", "false");
        props.put("mail.smtp.ssl.trust", "*");
//        props.put("mail.smtp.ssl.enable", "true");

        return mailSender;
    }

    //----------
    @Bean
    public Store getMailReciever (
            @Value("${app.imap.username}") String user,
            @Value("${app.imap.password}") String pass,
            @Value("${app.imap.host}")  String host,
            @Value("${app.imap.port}") Integer port

    ) throws MessagingException, IOException {

        Properties props = new Properties();
//        props.put("mail.debug", "true");
        props.put("mail.store.protocol", "imaps");
        Session session = Session.getInstance(props);

        Store store = session.getStore();
        store.connect(host, user, pass);

        return store;
//

    }








    //---
}