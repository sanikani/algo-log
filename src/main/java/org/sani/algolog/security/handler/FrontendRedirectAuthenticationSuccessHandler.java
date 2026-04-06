package org.sani.algolog.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class FrontendRedirectAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public FrontendRedirectAuthenticationSuccessHandler(String targetUrl) {
        setDefaultTargetUrl(targetUrl);
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
