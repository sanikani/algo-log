package org.sani.algolog.security.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class FrontendRedirectLogoutSuccessHandlerTest {

    @Test
    @DisplayName("logout success redirects to configured frontend URL")
    void redirectsToFrontendUrl() throws Exception {
        FrontendRedirectLogoutSuccessHandler handler =
                new FrontendRedirectLogoutSuccessHandler("http://localhost:5173");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onLogoutSuccess(request, response, null);

        assertThat(response.getRedirectedUrl()).isEqualTo("http://localhost:5173");
    }
}
