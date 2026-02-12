package org.sani.algolog.domain.solution.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sani.algolog.domain.member.entity.Member;
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

    // TODO: Problem 엔티티 작업 완료 후 연관관계 매핑으로 교체
    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Solution(String code, Integer timeElapsed, boolean isSolved, Long problemId, Member member) {
        this.code = code;
        this.timeElapsed = timeElapsed;
        this.isSolved = isSolved;
        this.problemId = problemId;
        this.member = member;
    }

    public void update(String code, Integer timeElapsed, boolean isSolved, Long problemId) {
        this.code = code;
        this.timeElapsed = timeElapsed;
        this.isSolved = isSolved;
        this.problemId = problemId;
    }
}
