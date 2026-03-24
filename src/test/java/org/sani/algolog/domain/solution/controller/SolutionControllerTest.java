package org.sani.algolog.domain.solution.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sani.algolog.domain.problem.dto.ProblemResponse;
import org.sani.algolog.domain.problem.entity.Platform;
import org.sani.algolog.domain.solution.dto.SolutionResponse;
import org.sani.algolog.domain.solution.service.SolutionService;
import org.sani.algolog.global.error.exception.UnauthorizedException;
import org.sani.algolog.security.oauth.AlgoLogAuthenticatedPrincipal;
import org.sani.algolog.global.config.WebConfig;
import org.sani.algolog.global.error.GlobalExceptionHandler;
import org.springframework.security.authentication.TestingAuthenticationToken;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SolutionController.class)
@Import({GlobalExceptionHandler.class, WebConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class SolutionControllerTest {

    private static final String BASE_URL = "/api/v1/solutions";

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
                  "memoMarkdown": "## 회고\\n- DP 점화식을 다시 복습해야 한다.",
                  "problem": {
                    "platform": "BOJ",
                    "externalProblemId": "1000",
                    "title": "A+B",
                    "problemUrl": "https://www.acmicpc.net/problem/1000",
                    "difficulty": "Bronze V"
                  }
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .with(authentication(authenticatedMember(1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.problem.id").value(10))
                .andExpect(jsonPath("$.data.problem.platform").value("BOJ"))
                .andExpect(jsonPath("$.data.memoMarkdown").value("회고"));
    }

    @Test
    @DisplayName("POST /api/v1/solutions validation failure returns INVALID_INPUT")
    void saveSolutionValidationFailure() throws Exception {
        String invalidBody = """
                {
                  "code": "",
                  "timeElapsed": -1,
                  "solved": null,
                  "memoMarkdown": "",
                  "problem": {
                    "title": ""
                  }
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .with(authentication(authenticatedMember(1L)))
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
                        .with(authentication(authenticatedMember(1L))))
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
                        .with(authentication(authenticatedMember(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.problem.title").value("A+B"));
    }

    @Test
    @DisplayName("GET /api/v1/solutions/{id} forbidden returns FORBIDDEN")
    void getSolutionForbidden() throws Exception {
        when(solutionService.getSolution(100L, 1L))
                .thenThrow(new AccessDeniedException("Access is denied."));

        mockMvc.perform(get(BASE_URL + "/100")
                        .with(authentication(authenticatedMember(1L))))
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
                        .with(authentication(authenticatedMember(1L))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("PUT /api/v1/solutions/{id} returns updated solution")
    void updateSolution() throws Exception {
        SolutionResponse response = solutionResponse(100L, 1L, 10L);
        when(solutionService.update(eq(100L), any(), eq(1L))).thenReturn(response);

        String body = """
                {
                  "code": "public class Main { public static void main(String[] args) {} }",
                  "timeElapsed": 111,
                  "solved": true,
                  "memoMarkdown": "수정된 회고"
                }
                """;

        mockMvc.perform(put(BASE_URL + "/100")
                        .with(authentication(authenticatedMember(1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.problem.id").value(10));
    }

    @Test
    @DisplayName("PUT /api/v1/solutions/{id} forbidden returns FORBIDDEN")
    void updateSolutionForbidden() throws Exception {
        when(solutionService.update(eq(100L), any(), eq(1L)))
                .thenThrow(new AccessDeniedException("작성자만 수정/삭제할 수 있습니다."));

        String body = """
                {
                  "code": "public class Main {}",
                  "timeElapsed": 120,
                  "solved": true,
                  "memoMarkdown": "회고"
                }
                """;

        mockMvc.perform(put(BASE_URL + "/100")
                        .with(authentication(authenticatedMember(1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("PUT /api/v1/solutions/{id} missing returns NOT_FOUND")
    void updateSolutionNotFound() throws Exception {
        when(solutionService.update(eq(100L), any(), eq(1L)))
                .thenThrow(new EntityNotFoundException("Solution not found: 100"));

        String body = """
                {
                  "code": "public class Main {}",
                  "timeElapsed": 120,
                  "solved": true,
                  "memoMarkdown": "회고"
                }
                """;

        mockMvc.perform(put(BASE_URL + "/100")
                        .with(authentication(authenticatedMember(1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("PUT /api/v1/solutions/{id} validation failure returns INVALID_INPUT")
    void updateSolutionValidationFailure() throws Exception {
        String invalidBody = """
                {
                  "code": "",
                  "timeElapsed": -1,
                  "solved": null,
                  "memoMarkdown": ""
                }
                """;

        mockMvc.perform(put(BASE_URL + "/100")
                        .with(authentication(authenticatedMember(1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
    }

    @Test
    @DisplayName("DELETE /api/v1/solutions/{id} returns SUCCESS")
    void deleteSolution() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/100")
                        .with(authentication(authenticatedMember(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("DELETE /api/v1/solutions/{id} forbidden returns FORBIDDEN")
    void deleteSolutionForbidden() throws Exception {
        doThrow(new AccessDeniedException("작성자만 수정/삭제할 수 있습니다."))
                .when(solutionService).delete(100L, 1L);

        mockMvc.perform(delete(BASE_URL + "/100")
                        .with(authentication(authenticatedMember(1L))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("DELETE /api/v1/solutions/{id} missing returns NOT_FOUND")
    void deleteSolutionNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Solution not found: 100"))
                .when(solutionService).delete(100L, 1L);

        mockMvc.perform(delete(BASE_URL + "/100")
                        .with(authentication(authenticatedMember(1L))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("missing authentication returns UNAUTHORIZED")
    void missingAuthentication() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("invalid path variable returns TYPE_MISMATCH")
    void invalidPathVariableType() throws Exception {
        mockMvc.perform(get(BASE_URL + "/abc")
                        .with(authentication(authenticatedMember(1L))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("TYPE_MISMATCH"));
    }

    @Test
    @DisplayName("invalid authenticated principal returns UNAUTHORIZED")
    void invalidAuthenticatedPrincipal() throws Exception {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("anonymous", null);
        authentication.setAuthenticated(true);

        mockMvc.perform(get(BASE_URL)
                        .with(authentication(authentication)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    private SolutionResponse solutionResponse(Long id, Long memberId, Long problemId) {
        LocalDateTime now = LocalDateTime.now();
        return SolutionResponse.builder()
                .id(id)
                .code("public class Main {}")
                .timeElapsed(120)
                .solved(true)
                .memoMarkdown("회고")
                .problem(ProblemResponse.builder()
                        .id(problemId)
                        .platform(Platform.BOJ)
                        .externalProblemId("1000")
                        .title("A+B")
                        .problemUrl("https://www.acmicpc.net/problem/1000")
                        .difficulty("Bronze V")
                        .build())
                .memberId(memberId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private TestingAuthenticationToken authenticatedMember(Long memberId) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(new TestPrincipal(memberId), null);
        authentication.setAuthenticated(true);
        return authentication;
    }

    private record TestPrincipal(Long memberId) implements AlgoLogAuthenticatedPrincipal {
        @Override
        public Long getMemberId() {
            return memberId;
        }
    }
}
