package org.sani.algolog.domain.solution.repository;

import org.sani.algolog.domain.solution.entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
    List<Solution> findAllByMemberId(Long memberId);

    List<Solution> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<Solution> findByIdAndMemberId(Long id, Long memberId);
}
