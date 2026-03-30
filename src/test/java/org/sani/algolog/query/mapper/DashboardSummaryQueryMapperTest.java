package org.sani.algolog.query.mapper;

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
import org.sani.algolog.domain.solution.repository.SolutionRepository;
import org.sani.algolog.query.dto.SummaryCountRow;
import org.sani.algolog.query.dto.SummaryTotalsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DashboardSummaryQueryMapperTest {

    @Autowired
    private DashboardSummaryQueryMapper dashboardSummaryQueryMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SolutionRepository solutionRepository;

    @Test
    @DisplayName("dashboard summary aggregates totals and group counts for a member")
    void findDashboardSummaryByMemberId() {
        Member owner = memberRepository.save(member("owner@example.com", "owner"));
        Member other = memberRepository.save(member("other@example.com", "other"));

        Problem bojGold = problemRepository.save(problem(Platform.BOJ, "1000", "Gold 4"));
        Problem programmersGold = problemRepository.save(problem(Platform.PROGRAMMERS, "2000", "Gold 4"));
        Problem bojSilver = problemRepository.save(problem(Platform.BOJ, "3000", "Silver 1"));

        saveSolution(owner, bojGold, true);
        saveSolution(owner, bojGold, false);
        saveSolution(owner, programmersGold, true);
        saveSolution(owner, bojSilver, true);
        saveSolution(other, bojGold, true);

        SummaryTotalsRow totals = dashboardSummaryQueryMapper.findSummaryTotalsByMemberId(owner.getId());
        List<SummaryCountRow> platformCounts = dashboardSummaryQueryMapper.findPlatformCountsByMemberId(owner.getId());
        List<SummaryCountRow> difficultyCounts = dashboardSummaryQueryMapper.findDifficultyCountsByMemberId(owner.getId());

        assertThat(totals.totalCount()).isEqualTo(4);
        assertThat(totals.solvedCount()).isEqualTo(3);
        assertThat(totals.failedCount()).isEqualTo(1);

        assertThat(platformCounts).containsExactly(
                new SummaryCountRow("BOJ", 3),
                new SummaryCountRow("PROGRAMMERS", 1)
        );

        assertThat(difficultyCounts).containsExactly(
                new SummaryCountRow("Gold 4", 3),
                new SummaryCountRow("Silver 1", 1)
        );
    }

    @Test
    @DisplayName("dashboard summary returns zeros and empty lists when member has no data")
    void findDashboardSummaryByMemberIdWhenEmpty() {
        Member owner = memberRepository.save(member("empty@example.com", "empty"));

        SummaryTotalsRow totals = dashboardSummaryQueryMapper.findSummaryTotalsByMemberId(owner.getId());
        List<SummaryCountRow> platformCounts = dashboardSummaryQueryMapper.findPlatformCountsByMemberId(owner.getId());
        List<SummaryCountRow> difficultyCounts = dashboardSummaryQueryMapper.findDifficultyCountsByMemberId(owner.getId());

        assertThat(totals.totalCount()).isEqualTo(0);
        assertThat(totals.solvedCount()).isEqualTo(0);
        assertThat(totals.failedCount()).isEqualTo(0);
        assertThat(platformCounts).isEmpty();
        assertThat(difficultyCounts).isEmpty();
    }

    private void saveSolution(Member member, Problem problem, boolean isSolved) {
        solutionRepository.save(Solution.builder()
                .code("public class Main {}")
                .timeElapsed(100)
                .isSolved(isSolved)
                .memoMarkdown("memo")
                .problem(problem)
                .member(member)
                .build());
    }

    private Member member(String email, String nickname) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();
    }

    private Problem problem(Platform platform, String externalProblemId, String difficulty) {
        return Problem.builder()
                .platform(platform)
                .externalProblemId(externalProblemId)
                .title("Sample")
                .problemUrl("https://example.com/problems/" + externalProblemId)
                .difficulty(difficulty)
                .build();
    }
}
