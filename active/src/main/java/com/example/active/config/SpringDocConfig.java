package com.example.active.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI();
    }

    static {
        // Isso remove os campos internos e mostra apenas page, size e sort
        org.springdoc.core.utils.SpringDocUtils.getConfig()
                .replaceParameterObjectWithClass(org.springframework.data.domain.Pageable.class,
                        org.springdoc.core.converters.models.Pageable.class);
    }
}
