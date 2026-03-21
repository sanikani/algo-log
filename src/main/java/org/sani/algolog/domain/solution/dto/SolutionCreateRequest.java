package org.sani.algolog.domain.solution.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sani.algolog.domain.member.entity.Member;
import org.sani.algolog.domain.problem.dto.ProblemRequest;
import org.sani.algolog.domain.problem.entity.Problem;
import org.sani.algolog.domain.solution.entity.Solution;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SolutionCreateRequest {

    @NotBlank
    private String code;

    @NotNull
    @Min(0)
    private Integer timeElapsed;

    @NotNull
    private Boolean solved;

    @NotBlank
    private String memoMarkdown;

    @Valid
    @NotNull
    private ProblemRequest problem;

    public Solution toEntity(Member member, Problem problem) {
        return Solution.builder()
                .code(code)
                .timeElapsed(timeElapsed)
                .isSolved(solved)
                .memoMarkdown(memoMarkdown)
                .problem(problem)
                .member(member)
                .build();
    }
}
