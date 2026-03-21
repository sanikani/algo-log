package org.sani.algolog.domain.solution.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SolutionUpdateRequest {

    @NotBlank
    private String code;

    @NotNull
    @Min(0)
    private Integer timeElapsed;

    @NotNull
    private Boolean solved;

    @NotBlank
    private String memoMarkdown;
}
