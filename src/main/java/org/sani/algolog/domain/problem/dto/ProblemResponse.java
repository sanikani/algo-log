package org.sani.algolog.domain.problem.dto;

import lombok.Builder;
import lombok.Getter;
import org.sani.algolog.domain.problem.entity.Platform;
import org.sani.algolog.domain.problem.entity.Problem;

@Getter
@Builder
public class ProblemResponse {

    private Long id;
    private Platform platform;
    private String externalProblemId;
    private String title;
    private String problemUrl;
    private String difficulty;

    public static ProblemResponse from(Problem problem) {
        return ProblemResponse.builder()
                .id(problem.getId())
                .platform(problem.getPlatform())
                .externalProblemId(problem.getExternalProblemId())
                .title(problem.getTitle())
                .problemUrl(problem.getProblemUrl())
                .difficulty(problem.getDifficulty())
                .build();
    }
}
