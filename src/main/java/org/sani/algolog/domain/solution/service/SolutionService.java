package org.sani.algolog.domain.solution.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.sani.algolog.domain.member.entity.Member;
import org.sani.algolog.domain.member.repository.MemberRepository;
import org.sani.algolog.domain.problem.entity.Problem;
import org.sani.algolog.domain.problem.repository.ProblemRepository;
import org.sani.algolog.domain.solution.dto.SolutionRequest;
import org.sani.algolog.domain.solution.dto.SolutionResponse;
import org.sani.algolog.domain.solution.entity.Solution;
import org.sani.algolog.domain.solution.repository.SolutionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;

    @Transactional
    public SolutionResponse save(SolutionRequest request, Long memberId) {
        Member member = findMember(memberId);
        Problem problem = findProblem(request.getProblemId());
        Solution solution = request.toEntity(member, problem);

        Solution savedSolution = solutionRepository.save(solution);
        return SolutionResponse.from(savedSolution);
    }

    @Transactional
    public SolutionResponse update(Long solutionId, SolutionRequest request, Long memberId) {
        Solution solution = findSolution(solutionId);
        validateOwner(solution, memberId);
        Problem problem = findProblem(request.getProblemId());

        solution.update(
                request.getCode(),
                request.getTimeElapsed(),
                request.getSolved(),
                problem
        );

        return SolutionResponse.from(solution);
    }

    @Transactional
    public void delete(Long solutionId, Long memberId) {
        Solution solution = findSolution(solutionId);
        validateOwner(solution, memberId);
        solutionRepository.delete(solution);
    }

    public List<SolutionResponse> getSolutionsByMember(Long memberId) {
        findMember(memberId);
        return solutionRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId).stream()
                .map(SolutionResponse::from)
                .toList();
    }

    public SolutionResponse getSolution(Long solutionId, Long memberId) {
        Solution solution = findSolution(solutionId);
        validateOwner(solution, memberId);
        return SolutionResponse.from(solution);
    }

    private Solution findSolution(Long solutionId) {
        return solutionRepository.findById(solutionId)
                .orElseThrow(() -> new EntityNotFoundException("Solution not found: " + solutionId));
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberId));
    }

    private Problem findProblem(Long problemId) {
        return problemRepository.findById(problemId)
                .orElseThrow(() -> new EntityNotFoundException("Problem not found: " + problemId));
    }

    private void validateOwner(Solution solution, Long memberId) {
        if (!solution.getMember().getId().equals(memberId)) {
            throw new AccessDeniedException("작성자만 수정/삭제할 수 있습니다.");
        }
    }
}
