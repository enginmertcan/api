package com.mertcanengin.api.service;

import com.mertcanengin.api.common.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String SENDGRID_ENDPOINT = "https://api.sendgrid.com/v3/mail/send";

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String fromAddress;

    public MailService(RestTemplateBuilder restTemplateBuilder,
                       @Value("${SENDGRID_API_KEY:}") String apiKey,
                       @Value("${app.mail.from:no-reply@lecture-portal.local}") String fromAddress) {
        this.restTemplate = restTemplateBuilder.build();
        this.apiKey = apiKey;
        this.fromAddress = fromAddress;
    }

    public void sendVerificationEmail(String to, String code) {
        sendPlainTextEmail(
                to,
                "Lecture Portal - E-posta Doğrulama",
                """
                        Merhaba,

                        Hesabını etkinleştirmek için doğrulama kodun: %s
                        Kod 15 dakika boyunca geçerlidir.

                        Teşekkürler,
                        Lecture Portal
                        """.formatted(code)
        );
    }

    public void sendMfaCodeEmail(String to, String code, long ttlMinutes) {
        sendPlainTextEmail(
                to,
                "Lecture Portal - MFA Doğrulama Kodu",
                """
                        Güvenli giriş için MFA kodun: %s
                        Kod %s dakika boyunca geçerlidir.
                        """.formatted(code, ttlMinutes)
        );
    }

    public void sendPasswordResetEmail(String to, String token, long ttlMinutes) {
        sendPlainTextEmail(
                to,
                "Lecture Portal - Parola Sıfırlama",
                """
                        Parolanı sıfırlamak için aşağıdaki kodu kullan:

                        %s

                        Kod %d dakika boyunca geçerlidir. Eğer bu isteği sen oluşturmadıysan, hesabını korumak için bizimle iletişime geç.
                        """.formatted(token, ttlMinutes)
        );
    }

    public void sendActivityAlertEmail(String to, String subject, String body) {
        sendPlainTextEmail(to, subject, body);
    }

    private void sendPlainTextEmail(String to, String subject, String content) {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("SendGrid API anahtarı tanımlı değil.");
            throw new GeneralException("Mail servisi yapılandırılmadı. Yönetici ile iletişime geç.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var body = SendGridRequest.builder()
                .from(fromAddress)
                .to(to)
                .subject(subject)
                .textContent(content)
                .build();

        HttpEntity<SendGridRequest> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(SENDGRID_ENDPOINT, request, Void.class);
            if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
                log.error("SendGrid responsed with status {} for {}", response.getStatusCode(), to);
                throw new GeneralException("E-posta gönderilemedi. Lütfen daha sonra tekrar deneyin.");
            }
        } catch (RestClientException ex) {
            log.error("Email could not be sent to {}", to, ex);
            throw new GeneralException("E-posta gönderilemedi. Lütfen daha sonra tekrar deneyin.");
        }
    }

    private record SendGridRequest(
            java.util.List<Personalization> personalizations,
            Email from,
            String subject,
            java.util.List<Content> content
    ) {
        private static Builder builder() {
            return new Builder();
        }

        private static class Builder {
            private String fromEmail;
            private String toEmail;
            private String subject;
            private String textContent;

            Builder from(String fromEmail) {
                this.fromEmail = fromEmail;
                return this;
            }

            Builder to(String toEmail) {
                this.toEmail = toEmail;
                return this;
            }

            Builder subject(String subject) {
                this.subject = subject;
                return this;
            }

            Builder textContent(String textContent) {
                this.textContent = textContent;
                return this;
            }

            SendGridRequest build() {
                Content content = new Content("text/plain", textContent);
                Personalization personalization = new Personalization(java.util.List.of(new Email(toEmail)));
                return new SendGridRequest(
                        java.util.List.of(personalization),
                        new Email(fromEmail),
                        subject,
                        java.util.List.of(content)
                );
            }
        }
    }

    private record Personalization(java.util.List<Email> to) {
    }

    private record Email(String email) {
    }

    private record Content(String type, String value) {
    }
}
