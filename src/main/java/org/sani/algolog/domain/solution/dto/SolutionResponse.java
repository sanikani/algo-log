package org.sani.algolog.domain.solution.dto;

import lombok.Builder;
import lombok.Getter;
import org.sani.algolog.domain.problem.dto.ProblemResponse;
import org.sani.algolog.domain.solution.entity.Solution;

import java.time.LocalDateTime;

@Getter
@Builder
public class SolutionResponse {

    private Long id;
    private String code;
    private Integer timeElapsed;
    private boolean solved;
    private String memoMarkdown;
    private ProblemResponse problem;
    private Long memberId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SolutionResponse from(Solution solution) {
        return SolutionResponse.builder()
                .id(solution.getId())
                .code(solution.getCode())
                .timeElapsed(solution.getTimeElapsed())
                .solved(solution.isSolved())
                .memoMarkdown(solution.getMemoMarkdown())
                .problem(ProblemResponse.from(solution.getProblem()))
                .memberId(solution.getMember().getId())
                .createdAt(solution.getCreatedAt())
                .updatedAt(solution.getUpdatedAt())
                .build();
    }
}
