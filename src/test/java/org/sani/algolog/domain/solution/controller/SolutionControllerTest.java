package org.sani.algolog.domain.solution.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sani.algolog.domain.solution.dto.SolutionResponse;
import org.sani.algolog.domain.solution.service.SolutionService;
import org.sani.algolog.global.error.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SolutionController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class SolutionControllerTest {

    private static final String BASE_URL = "/api/v1/solutions";
    private static final String MEMBER_ID_HEADER = "X-Member-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SolutionService solutionService;

    @Test
    @DisplayName("POST /api/v1/solutions returns SUCCESS")
    void saveSolution() throws Exception {
        SolutionResponse response = solutionResponse(100L, 1L, 10L);
        when(solutionService.save(any(), eq(1L))).thenReturn(response);

        String body = """
                {
                  "code": "public class Main {}",
                  "timeElapsed": 123,
                  "solved": true,
                  "problemId": 10
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .header(MEMBER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.problemId").value(10));
    }

    @Test
    @DisplayName("POST /api/v1/solutions validation failure returns INVALID_INPUT")
    void saveSolutionValidationFailure() throws Exception {
        String invalidBody = """
                {
                  "code": "",
                  "timeElapsed": -1,
                  "solved": null
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .header(MEMBER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
    }

    @Test
    @DisplayName("GET /api/v1/solutions returns list")
    void getSolutions() throws Exception {
        SolutionResponse first = solutionResponse(11L, 1L, 101L);
        SolutionResponse second = solutionResponse(10L, 1L, 100L);
        when(solutionService.getSolutionsByMember(1L)).thenReturn(List.of(first, second));

        mockMvc.perform(get(BASE_URL)
                        .header(MEMBER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(11))
                .andExpect(jsonPath("$.data[1].id").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/solutions/{id} returns detail")
    void getSolution() throws Exception {
        when(solutionService.getSolution(100L, 1L)).thenReturn(solutionResponse(100L, 1L, 10L));

        mockMvc.perform(get(BASE_URL + "/100")
                        .header(MEMBER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(100));
    }

    @Test
    @DisplayName("GET /api/v1/solutions/{id} forbidden returns FORBIDDEN")
    void getSolutionForbidden() throws Exception {
        when(solutionService.getSolution(100L, 1L))
                .thenThrow(new AccessDeniedException("Access is denied."));

        mockMvc.perform(get(BASE_URL + "/100")
                        .header(MEMBER_ID_HEADER, "1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("GET /api/v1/solutions/{id} missing returns NOT_FOUND")
    void getSolutionNotFound() throws Exception {
        when(solutionService.getSolution(100L, 1L))
                .thenThrow(new EntityNotFoundException("Solution not found: 100"));

        mockMvc.perform(get(BASE_URL + "/100")
                        .header(MEMBER_ID_HEADER, "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("missing X-Member-Id header returns MISSING_PARAMETER")
    void missingMemberIdHeader() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("MISSING_PARAMETER"));
    }

    private SolutionResponse solutionResponse(Long id, Long memberId, Long problemId) {
        LocalDateTime now = LocalDateTime.now();
        return SolutionResponse.builder()
                .id(id)
                .code("public class Main {}")
                .timeElapsed(120)
                .solved(true)
                .problemId(problemId)
                .memberId(memberId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
