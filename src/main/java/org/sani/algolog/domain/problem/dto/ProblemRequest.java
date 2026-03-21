package org.sani.algolog.domain.problem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 100)
    private String externalProblemId;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 500)
    private String problemUrl;

    @NotBlank
    @Size(max = 50)
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
