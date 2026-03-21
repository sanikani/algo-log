package org.sani.algolog.domain.solution.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sani.algolog.domain.member.entity.Member;
import org.sani.algolog.domain.member.entity.Provider;
import org.sani.algolog.domain.member.entity.Role;
import org.sani.algolog.domain.member.repository.MemberRepository;
import org.sani.algolog.domain.problem.entity.Platform;
import org.sani.algolog.domain.problem.entity.Problem;
import org.sani.algolog.domain.problem.repository.ProblemRepository;
import org.sani.algolog.domain.solution.entity.Solution;
import org.sani.algolog.global.config.JpaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class SolutionRepositoryTest {

    @Autowired
    private SolutionRepository solutionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Test
    @DisplayName("풀이 저장 및 회원 연관관계 매핑 확인")
    void saveSolutionWithMember() {
        // given
        Member member = memberRepository.save(Member.builder()
                .email("solution@test.com")
                .nickname("풀이작성자")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build());
        Problem problem = problemRepository.save(Problem.builder()
                .platform(Platform.BOJ)
                .externalProblemId("1000")
                .title("A+B")
                .problemUrl("https://www.acmicpc.net/problem/1000")
                .difficulty("Bronze V")
                .build());

        Solution solution = Solution.builder()
                .code("public class Solution { }")
                .timeElapsed(120)
                .isSolved(true)
                .memoMarkdown("기본 입출력 회고")
                .problem(problem)
                .member(member)
                .build();

        // when
        Solution savedSolution = solutionRepository.save(solution);

        // then
        assertThat(savedSolution.getId()).isNotNull();
        assertThat(savedSolution.getMember().getId()).isEqualTo(member.getId());
        assertThat(savedSolution.getProblem().getId()).isEqualTo(problem.getId());
        assertThat(savedSolution.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("회원 ID로 풀이 목록을 최신순으로 조회하면서 문제 연관관계를 함께 가져온다")
    void findAllByMemberIdOrderByCreatedAtDesc() {
        // given
        Member member = memberRepository.save(Member.builder()
                .email("list@test.com")
                .nickname("풀이목록")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build());
        Problem problem = problemRepository.save(Problem.builder()
                .platform(Platform.BOJ)
                .externalProblemId("1001")
                .title("A-B")
                .problemUrl("https://www.acmicpc.net/problem/1001")
                .difficulty("Bronze V")
                .build());

        Problem newerProblem = problemRepository.save(Problem.builder()
                .platform(Platform.BOJ)
                .externalProblemId("1002")
                .title("A/B")
                .problemUrl("https://www.acmicpc.net/problem/1002")
                .difficulty("Bronze IV")
                .build());

        Solution firstSolution = solutionRepository.save(Solution.builder()
                .code("class Solution { }")
                .timeElapsed(300)
                .isSolved(true)
                .memoMarkdown("입출력 풀이")
                .problem(problem)
                .member(member)
                .build());

        Solution secondSolution = solutionRepository.save(Solution.builder()
                .code("class AnotherSolution { }")
                .timeElapsed(150)
                .isSolved(true)
                .memoMarkdown("사칙연산 풀이")
                .problem(newerProblem)
                .member(member)
                .build());

        // when
        List<Solution> solutions = solutionRepository.findAllByMemberIdOrderByCreatedAtDesc(member.getId());

        // then
        assertThat(solutions).hasSize(2);
        assertThat(solutions.get(0).getId()).isEqualTo(secondSolution.getId());
        assertThat(solutions.get(1).getId()).isEqualTo(firstSolution.getId());
        assertThat(solutions.get(0).getProblem().getExternalProblemId()).isEqualTo("1002");
        assertThat(solutions.get(1).getProblem().getExternalProblemId()).isEqualTo("1001");
    }

    @Test
    @DisplayName("풀이 ID와 회원 ID로 상세 조회하면서 문제 연관관계를 함께 가져온다")
    void findByIdAndMemberId() {
        Member member = memberRepository.save(Member.builder()
                .email("detail@test.com")
                .nickname("상세조회")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build());
        Problem problem = problemRepository.save(Problem.builder()
                .platform(Platform.BOJ)
                .externalProblemId("2557")
                .title("Hello World")
                .problemUrl("https://www.acmicpc.net/problem/2557")
                .difficulty("Bronze V")
                .build());

        Solution solution = solutionRepository.save(Solution.builder()
                .code("public class Main {}")
                .timeElapsed(10)
                .isSolved(true)
                .memoMarkdown("기본 출력 문제")
                .problem(problem)
                .member(member)
                .build());

        Solution found = solutionRepository.findByIdAndMemberId(solution.getId(), member.getId())
                .orElseThrow(() -> new IllegalArgumentException("풀이가 없습니다."));

        assertThat(found.getId()).isEqualTo(solution.getId());
        assertThat(found.getProblem().getTitle()).isEqualTo("Hello World");
        assertThat(found.getProblem().getExternalProblemId()).isEqualTo("2557");
    }
}
