package org.sani.algolog.domain.problem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sani.algolog.domain.problem.entity.Platform;
import org.sani.algolog.domain.problem.entity.Problem;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemRequest {

    @NotNull
    private Platform platform;

    @NotBlank
    private String externalProblemId;

    @NotBlank
    private String title;

    @NotBlank
    private String problemUrl;

    @NotBlank
    private String difficulty;

    public Problem toEntity() {
        return Problem.builder()
                .platform(platform)
                .externalProblemId(externalProblemId)
                .title(title)
                .problemUrl(problemUrl)
                .difficulty(difficulty)
                .build();
    }
}
