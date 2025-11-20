package com.mertcanengin.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lectureManagementApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lecture Management API")
                        .description("Ders/öğrenci yönetim servisi için dokümantasyon")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Lecture Management Team")
                                .email("support@example.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Kaynak kodu")
                        .url("https://github.com/mertcanengin/lecture-management"));
    }
}
