package org.sani.algolog.domain.solution.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sani.algolog.domain.member.entity.Member;
import org.sani.algolog.domain.problem.entity.Problem;
import org.sani.algolog.global.error.exception.BadRequestException;
import org.sani.algolog.global.common.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Solution extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String code;

    private Integer timeElapsed;

    private boolean isSolved;

    @Lob
    @Column
    private String memoMarkdown;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Builder
    public Solution(
            String code,
            Integer timeElapsed,
            boolean isSolved,
            String memoMarkdown,
            Problem problem,
            Member member
    ) {
        this.code = code;
        this.timeElapsed = timeElapsed;
        this.isSolved = isSolved;
        this.memoMarkdown = memoMarkdown;
        this.problem = validateProblem(problem);
        this.member = member;
    }

    public void update(String code, Integer timeElapsed, boolean isSolved, String memoMarkdown) {
        this.code = code;
        this.timeElapsed = timeElapsed;
        this.isSolved = isSolved;
        this.memoMarkdown = memoMarkdown;
    }

    private Problem validateProblem(Problem problem) {
        if (problem == null) {
            throw new BadRequestException("Problem must not be null.");
        }
        return problem;
    }
}
