package org.sani.algolog.domain.dashboard.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sani.algolog.domain.dashboard.dto.HeatmapDayResponse;
import org.sani.algolog.domain.dashboard.service.DashboardQueryService;
import org.sani.algolog.global.config.WebConfig;
import org.sani.algolog.global.error.GlobalExceptionHandler;
import org.sani.algolog.security.oauth.AlgoLogAuthenticatedPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DashboardController.class)
@Import({GlobalExceptionHandler.class, WebConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardQueryService dashboardQueryService;

    @Test
    @DisplayName("GET /api/v1/dashboard/heatmap returns heatmap data")
    void getHeatmap() throws Exception {
        when(dashboardQueryService.getHeatmap(1L)).thenReturn(List.of(
                new HeatmapDayResponse(LocalDate.of(2026, 3, 28), 2),
                new HeatmapDayResponse(LocalDate.of(2026, 3, 29), 1)
        ));

        mockMvc.perform(get("/api/v1/dashboard/heatmap")
                        .with(authentication(authenticatedMember(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].date").value("2026-03-28"))
                .andExpect(jsonPath("$.data[0].solvedCount").value(2))
                .andExpect(jsonPath("$.data[1].date").value("2026-03-29"))
                .andExpect(jsonPath("$.data[1].solvedCount").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/heatmap returns NOT_FOUND when member is missing")
    void getHeatmapMemberNotFound() throws Exception {
        when(dashboardQueryService.getHeatmap(1L))
                .thenThrow(new EntityNotFoundException("Member not found: 1"));

        mockMvc.perform(get("/api/v1/dashboard/heatmap")
                        .with(authentication(authenticatedMember(1L))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    private TestingAuthenticationToken authenticatedMember(Long memberId) {
        AlgoLogAuthenticatedPrincipal principal = () -> memberId;
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(principal, null);
        authentication.setAuthenticated(true);
        return authentication;
    }
}
