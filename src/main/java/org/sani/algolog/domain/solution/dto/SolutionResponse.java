package org.sani.algolog.domain.solution.dto;

import lombok.Builder;
import lombok.Getter;
import org.sani.algolog.domain.solution.entity.Solution;

import java.time.LocalDateTime;

@Getter
@Builder
public class SolutionResponse {

    private Long id;
    private String code;
    private Integer timeElapsed;
    private boolean solved;
    private Long problemId;
    private Long memberId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SolutionResponse from(Solution solution) {
        return SolutionResponse.builder()
                .id(solution.getId())
                .code(solution.getCode())
                .timeElapsed(solution.getTimeElapsed())
                .solved(solution.isSolved())
                .problemId(solution.getProblem().getId())
                .memberId(solution.getMember().getId())
                .createdAt(solution.getCreatedAt())
                .updatedAt(solution.getUpdatedAt())
                .build();
    }
}
