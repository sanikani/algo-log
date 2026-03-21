package org.sani.algolog.domain.problem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sani.algolog.global.common.BaseEntity;

@Entity
@Table(
        name = "problems",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_problem_platform_external_id",
                        columnNames = {"platform", "external_problem_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Platform platform;

    @Column(name = "external_problem_id", nullable = false, length = 100)
    private String externalProblemId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 500)
    private String problemUrl;

    @Column(nullable = false, length = 50)
    private String difficulty;

    @Builder
    public Problem(
            Platform platform,
            String externalProblemId,
            String title,
            String problemUrl,
            String difficulty
    ) {
        this.platform = platform;
        this.externalProblemId = externalProblemId;
        this.title = title;
        this.problemUrl = problemUrl;
        this.difficulty = difficulty;
    }
}
