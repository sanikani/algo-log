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

    private static final String GITHUB_OAUTH_SCHEME = "githubOAuth";

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme githubOAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("GitHub OAuth2 login for authenticated AlgoLog APIs.")
                .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                        .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                                .authorizationUrl("https://github.com/login/oauth/authorize")
                                .tokenUrl("https://github.com/login/oauth/access_token")
                                .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                        .addString("read:user", "Read GitHub profile")
                                        .addString("user:email", "Read GitHub email"))));

        return new OpenAPI()
                .info(new Info()
                        .title("AlgoLog API")
                        .version("v1")
                        .description("AlgoLog REST API documentation"))
                .components(new Components().addSecuritySchemes(GITHUB_OAUTH_SCHEME, githubOAuthScheme))
                .addSecurityItem(new SecurityRequirement().addList(GITHUB_OAUTH_SCHEME));
    }
}
