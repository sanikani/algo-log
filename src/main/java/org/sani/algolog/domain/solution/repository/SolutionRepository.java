package org.sani.algolog.domain.solution.repository;

import org.sani.algolog.domain.solution.entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
    List<Solution> findAllByMemberId(Long memberId);

    @Query("""
            select s
            from Solution s
            join fetch s.problem p
            where s.member.id = :memberId
            order by s.createdAt desc
            """)
    List<Solution> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    @Query("""
            select s
            from Solution s
            join fetch s.problem p
            where s.id = :id
              and s.member.id = :memberId
            """)
    Optional<Solution> findByIdAndMemberId(Long id, Long memberId);
}
