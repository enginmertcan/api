package com.mertcanengin.api.service;

import com.mertcanengin.api.common.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public MailService(JavaMailSender mailSender,
                       @Value("${app.mail.from:no-reply@lecture-portal.local}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("Lecture Portal - E-posta Doğrulama");
        message.setText("Merhaba,\n\nHesabını etkinleştirmek için doğrulama kodun: " + code +
                "\nKod 15 dakika boyunca geçerlidir.\n\nTeşekkürler,\nLecture Portal");
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.error("Verification email could not be sent to {}", to, ex);
            throw new GeneralException("Doğrulama e-postası gönderilemedi. Lütfen daha sonra tekrar deneyin.");
        }
    }
}
