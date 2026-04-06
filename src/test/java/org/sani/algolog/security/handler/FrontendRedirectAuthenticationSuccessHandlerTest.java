package org.sani.algolog.security.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;

class FrontendRedirectAuthenticationSuccessHandlerTest {

    @Test
    @DisplayName("authentication success redirects to configured frontend URL")
    void redirectsToFrontendUrl() throws Exception {
        FrontendRedirectAuthenticationSuccessHandler handler =
                new FrontendRedirectAuthenticationSuccessHandler("http://localhost:5173");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(
                request,
                response,
                new TestingAuthenticationToken("principal", "credentials")
        );

        assertThat(response.getRedirectedUrl()).isEqualTo("http://localhost:5173");
    }
}
