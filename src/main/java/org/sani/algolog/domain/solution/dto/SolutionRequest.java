package org.sani.algolog.domain.solution.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sani.algolog.domain.member.entity.Member;
import org.sani.algolog.domain.solution.entity.Solution;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SolutionRequest {

    @NotBlank
    private String code;

    @NotNull
    @Min(0)
    private Integer timeElapsed;

    @NotNull
    private Boolean solved;

    @NotNull
    private Long problemId;

    public Solution toEntity(Member member){
        return Solution.builder()
                .code(this.getCode())
                .timeElapsed(this.getTimeElapsed())
                .isSolved(this.getSolved())
                .problemId(this.getProblemId())
                .member(member)
                .build();
    }
}
