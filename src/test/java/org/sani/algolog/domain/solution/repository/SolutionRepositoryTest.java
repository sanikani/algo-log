package org.sani.algolog.domain.solution.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sani.algolog.domain.member.entity.Member;
import org.sani.algolog.domain.member.entity.Provider;
import org.sani.algolog.domain.member.entity.Role;
import org.sani.algolog.domain.member.repository.MemberRepository;
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

        Solution solution = Solution.builder()
                .code("public class Solution { }")
                .timeElapsed(120)
                .isSolved(true)
                .problemId(1L)
                .member(member)
                .build();

        // when
        Solution savedSolution = solutionRepository.save(solution);

        // then
        assertThat(savedSolution.getId()).isNotNull();
        assertThat(savedSolution.getMember().getId()).isEqualTo(member.getId());
        assertThat(savedSolution.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("회원 ID로 풀이 목록 조회 성공")
    void findAllByMemberId() {
        // given
        Member member = memberRepository.save(Member.builder()
                .email("list@test.com")
                .nickname("풀이목록")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build());

        solutionRepository.save(Solution.builder()
                .code("class Solution { }")
                .timeElapsed(300)
                .isSolved(true)
                .problemId(1L)
                .member(member)
                .build());

        // when
        List<Solution> solutions = solutionRepository.findAllByMemberId(member.getId());

        // then
        assertThat(solutions).hasSize(1);
        assertThat(solutions.get(0).getProblemId()).isEqualTo(1L);
    }
}
