package org.sani.algolog.domain.problem.repository;

import org.sani.algolog.domain.problem.entity.Platform;
import org.sani.algolog.domain.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Optional<Problem> findByPlatformAndExternalProblemId(Platform platform, String externalProblemId);
}
