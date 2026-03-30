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
import org.sani.algolog.query.dto.HeatmapDailyCountRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HeatmapQueryMapperTest {

    @Autowired
    private HeatmapQueryMapper heatmapQueryMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SolutionRepository solutionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("member heatmap is grouped by created date and ordered ascending")
    void findDailyCountsByMemberIdAndDateRange() {
        Member owner = memberRepository.save(member("owner@example.com", "owner"));
        Member other = memberRepository.save(member("other@example.com", "other"));
        Problem problem = problemRepository.save(problem("1000"));

        saveSolution(owner, problem, LocalDateTime.of(2026, 3, 20, 10, 0));
        saveSolution(owner, problem, LocalDateTime.of(2026, 3, 20, 18, 0));
        saveSolution(owner, problem, LocalDateTime.of(2026, 3, 21, 9, 0));
        saveSolution(owner, problem, false, LocalDateTime.of(2026, 3, 21, 10, 0));
        saveSolution(other, problem, LocalDateTime.of(2026, 3, 20, 12, 0));
        saveSolution(owner, problem, LocalDateTime.of(2026, 3, 22, 0, 0));

        List<HeatmapDailyCountRow> rows = heatmapQueryMapper.findDailyCountsByMemberIdAndDateRange(
                owner.getId(),
                LocalDateTime.of(2026, 3, 19, 0, 0),
                LocalDateTime.of(2026, 3, 22, 0, 0)
        );

        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).solvedDate()).isEqualTo(LocalDate.of(2026, 3, 20));
        assertThat(rows.get(0).solvedCount()).isEqualTo(2);
        assertThat(rows.get(1).solvedDate()).isEqualTo(LocalDate.of(2026, 3, 21));
        assertThat(rows.get(1).solvedCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("member heatmap returns empty list when there is no data in range")
    void findDailyCountsByMemberIdAndDateRangeWhenEmpty() {
        Member owner = memberRepository.save(member("empty@example.com", "empty"));

        List<HeatmapDailyCountRow> rows = heatmapQueryMapper.findDailyCountsByMemberIdAndDateRange(
                owner.getId(),
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 2, 1, 0, 0)
        );

        assertThat(rows).isEmpty();
    }

    private void saveSolution(Member member, Problem problem, LocalDateTime createdAt) {
        saveSolution(member, problem, true, createdAt);
    }

    private void saveSolution(Member member, Problem problem, boolean isSolved, LocalDateTime createdAt) {
        Solution solution = solutionRepository.save(Solution.builder()
                .code("public class Main {}")
                .timeElapsed(100)
                .isSolved(isSolved)
                .memoMarkdown("memo")
                .problem(problem)
                .member(member)
                .build());

        jdbcTemplate.update(
                "UPDATE solution SET created_at = ?, updated_at = ? WHERE id = ?",
                Timestamp.valueOf(createdAt),
                Timestamp.valueOf(createdAt),
                solution.getId()
        );
    }

    private Member member(String email, String nickname) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();
    }

    private Problem problem(String externalProblemId) {
        return Problem.builder()
                .platform(Platform.BOJ)
                .externalProblemId(externalProblemId)
                .title("A+B")
                .problemUrl("https://www.acmicpc.net/problem/" + externalProblemId)
                .difficulty("Bronze V")
                .build();
    }
}
