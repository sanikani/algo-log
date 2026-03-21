package org.sani.algolog.domain.problem.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sani.algolog.domain.problem.dto.ProblemRequest;
import org.sani.algolog.domain.problem.entity.Platform;
import org.sani.algolog.domain.problem.entity.Problem;
import org.sani.algolog.domain.problem.repository.ProblemRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @InjectMocks
    private ProblemService problemService;

    @Test
    @DisplayName("existing problem is reused")
    void getOrCreateReturnsExistingProblem() {
        ProblemRequest request = request();
        Problem existingProblem = problem(1L);

        when(problemRepository.findByPlatformAndExternalProblemId(Platform.BOJ, "1000"))
                .thenReturn(Optional.of(existingProblem));

        Problem result = problemService.getOrCreate(request);

        assertThat(result).isEqualTo(existingProblem);
    }

    @Test
    @DisplayName("duplicate insert falls back to existing problem")
    void getOrCreateFallsBackAfterDuplicateInsert() {
        ProblemRequest request = request();
        Problem existingProblem = problem(1L);

        when(problemRepository.findByPlatformAndExternalProblemId(Platform.BOJ, "1000"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existingProblem));
        when(problemRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(Problem.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        Problem result = problemService.getOrCreate(request);

        assertThat(result).isEqualTo(existingProblem);
        verify(problemRepository).saveAndFlush(org.mockito.ArgumentMatchers.any(Problem.class));
    }

    private ProblemRequest request() {
        return new ProblemRequest(
                Platform.BOJ,
                "1000",
                "A+B",
                "https://www.acmicpc.net/problem/1000",
                "Bronze V"
        );
    }

    private Problem problem(Long id) {
        Problem problem = request().toEntity();
        ReflectionTestUtils.setField(problem, "id", id);
        return problem;
    }
}
