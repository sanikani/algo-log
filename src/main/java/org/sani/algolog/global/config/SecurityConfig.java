package org.sani.algolog.global.config;

import lombok.RequiredArgsConstructor;
import org.sani.algolog.security.handler.ApiAccessDeniedHandler;
import org.sani.algolog.security.handler.ApiAuthenticationEntryPoint;
import org.sani.algolog.security.handler.FrontendRedirectAuthenticationSuccessHandler;
import org.sani.algolog.security.handler.FrontendRedirectLogoutSuccessHandler;
import org.sani.algolog.security.oauth.CustomOAuth2UserService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;
    private final ApiAccessDeniedHandler apiAccessDeniedHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Value("${algolog.frontend.app-url:http://localhost:5173}")
    private String frontendAppUrl;

    @Value("${algolog.frontend.allowed-origin:http://localhost:5173}")
    private String frontendAllowedOrigin;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> logout.logoutSuccessHandler(
                        new FrontendRedirectLogoutSuccessHandler(frontendAppUrl)
                ))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/error",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/login/**",
                                "/oauth2/**"
                        ).permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                apiAuthenticationEntryPoint,
                                new AntPathRequestMatcher("/api/**")
                        )
                        .defaultAccessDeniedHandlerFor(
                                apiAccessDeniedHandler,
                                new AntPathRequestMatcher("/api/**")
                        )
                );

        if (clientRegistrationRepositoryProvider.getIfAvailable() != null) {
            http.oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                    .loginPage("/oauth2/authorization/github")
                    .successHandler(new FrontendRedirectAuthenticationSuccessHandler(frontendAppUrl))
            );
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendAllowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/logout", configuration);
        source.registerCorsConfiguration("/oauth2/**", configuration);
        return source;
    }
}
