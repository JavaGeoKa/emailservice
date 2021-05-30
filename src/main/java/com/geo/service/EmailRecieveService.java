package com.geo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


//@Service
public class EmailRecieveService {

    @Autowired
    private Store store;

    @Value("${app.imap.patchfolder}")private String patchfolder;
    static Map<String, InputStream> fileStreams = new HashMap<>();




    @Scheduled(fixedDelay = 5000)
    public void sendSimpleMessage() throws MessagingException, IOException {
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
        Message[] messages = inbox.getMessages();

        Arrays.stream(inbox.getMessages()).forEach(m -> {
            try {
                if (!m.getFlags().contains(Flags.Flag.DELETED)) {
                    try {
                        Object content = m.getContent();
                        if (content instanceof String) {
                            System.out.println("Simple Handle");

                            Address[] froms = m.getFrom();
                            String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
                            System.out.println(email);
                            System.out.println(m.getSubject());
                            System.out.println(content);

                            //TODO handle



                        } else if (content instanceof Multipart) {
                            System.out.println("Multipart Handle");
                            Address[] froms = m.getFrom();
                            String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
                            System.out.println(email);
                            System.out.println(m.getSubject());
                            System.out.println(((Multipart) content).getBodyPart(0).getContent());

                            fileStreams.clear();
                            for (int i = 1; i < ((Multipart) content).getCount(); i++) {
                                fileStreams.put(((Multipart) content).getBodyPart(i).getFileName(),
                                        ((Multipart) content).getBodyPart(i).getInputStream());
                            }


                            zipCurrentAttachments(m, email);

                            //TODO handle

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                }

            } catch (MessagingException e) {
                e.printStackTrace();
            }

            try {
                m.setFlag(Flags.Flag.DELETED, true);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        });


    }






    private void zipCurrentAttachments(Message m, String email) throws IOException, MessagingException {

            String zipFileName = patchfolder + email + m.getSentDate();
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);


            fileStreams.entrySet().stream().filter(e -> e.getKey()!= null).forEach(e -> {
                try {
                    zos.putNextEntry(new ZipEntry(e.getKey()));
                    byte[] bytes = e.getValue().readAllBytes();
                    zos.write(bytes, 0, bytes.length);
                    zos.closeEntry();

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            });

            zos.close();
            fos.close();



        }





}





