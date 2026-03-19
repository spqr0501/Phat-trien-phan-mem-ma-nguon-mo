package com.mycompany.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI pctechOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PCTech Store API")
                        .description("RESTful API cho hệ thống quản lý cửa hàng linh kiện máy tính PCTech Store")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("PCTech Store")
                                .email("support@pctech.store")));
    }
}
