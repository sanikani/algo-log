package org.sani.algolog.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String MEMBER_ID_SCHEME = "memberIdHeader";
    private static final String MEMBER_ID_HEADER = "X-Member-Id";

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme memberIdScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(MEMBER_ID_HEADER)
                .description("Temporary member identification header for MVP APIs.");

        return new OpenAPI()
                .info(new Info()
                        .title("AlgoLog API")
                        .version("v1")
                        .description("AlgoLog REST API documentation"))
                .components(new Components().addSecuritySchemes(MEMBER_ID_SCHEME, memberIdScheme))
                .addSecurityItem(new SecurityRequirement().addList(MEMBER_ID_SCHEME));
    }
}
