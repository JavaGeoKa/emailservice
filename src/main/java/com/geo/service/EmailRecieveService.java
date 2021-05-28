package com.geo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class EmailRecieveService {

    @Autowired
    private Store store;

    @Value("${app.imap.patchfolder}")private String patchfolder;
    static Map<String, InputStream> fileStreams = new HashMap<>();


    @Scheduled(fixedDelay = 5000)
    public void sendSimpleMessage() throws MessagingException, IOException {

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);


        Arrays.stream(inbox.getMessages()).forEach(m -> {

            try {
                Object content = m.getContent();
                if (content instanceof String) {
//                    Address[] in = m.getFrom();
//                    for (Address address : in) {
//                        System.out.println("FROM:" + address.toString());
//                    }
                    Arrays.stream(m.getFrom()).forEach(System.out::println);
                    System.out.println("subject: " + m.getSubject()
                            + "\nsend date: " + m.getSentDate()
                            + "\ncontent: "  +  (String)content);
                } else if (content instanceof Multipart) {

                    ArrayList <BodyPart> bps = new ArrayList<>();
                    for (int i = 0; i < ((Multipart) content).getCount(); i++) {
                        fileStreams.put(((Multipart) content).getBodyPart(i).getFileName(),
                                ((Multipart) content).getBodyPart(i).getInputStream());
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        });

        String zipFileName = patchfolder + "out.zip";
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
        System.exit(0);




    }



//    private void writeFileOnDisk(BodyPart bp) throws IOException, MessagingException {
//
//        System.out.println("size " + bp.getSize());
//        FileOutputStream fos = new FileOutputStream("/home/g/Downloads/files/" + bp.getFileName());
//        DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
//        outStream.write(bp.getInputStream().readAllBytes());
//        outStream.close();
//
//        System.out.println("write: " + bp.getFileName());
//
//    }

}
