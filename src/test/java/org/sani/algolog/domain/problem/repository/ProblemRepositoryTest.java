package org.sani.algolog.domain.problem.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sani.algolog.domain.problem.entity.Platform;
import org.sani.algolog.domain.problem.entity.Problem;
import org.sani.algolog.global.config.JpaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class ProblemRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Test
    @DisplayName("플랫폼과 외부 문제 ID로 문제 조회 성공")
    void findByPlatformAndExternalProblemId() {
        Problem savedProblem = problemRepository.save(Problem.builder()
                .platform(Platform.BOJ)
                .externalProblemId("1000")
                .title("A+B")
                .problemUrl("https://www.acmicpc.net/problem/1000")
                .difficulty("Bronze V")
                .build());

        Problem problem = problemRepository.findByPlatformAndExternalProblemId(Platform.BOJ, "1000")
                .orElseThrow(() -> new IllegalArgumentException("문제가 없습니다."));

        assertThat(problem.getId()).isEqualTo(savedProblem.getId());
        assertThat(problem.getTitle()).isEqualTo("A+B");
    }
}
