package mh.cyb.root.DpiBatchMeetBackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("DPI Batch Community Platform API")
                                                .version("1.0")
                                                .description("API documentation for DPI Batch Meet system"))
                                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                                .components(new Components()
                                                .addSecuritySchemes("Bearer Authentication", createSecurityScheme()));
        }

        private SecurityScheme createSecurityScheme() {
                return new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT");
        }
}
