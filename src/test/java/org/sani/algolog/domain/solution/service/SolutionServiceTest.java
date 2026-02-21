package org.sani.algolog.domain.solution.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sani.algolog.domain.member.entity.Member;
import org.sani.algolog.domain.member.repository.MemberRepository;
import org.sani.algolog.domain.solution.dto.SolutionRequest;
import org.sani.algolog.domain.solution.dto.SolutionResponse;
import org.sani.algolog.domain.solution.entity.Solution;
import org.sani.algolog.domain.solution.repository.SolutionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolutionServiceTest {

    @Mock
    private SolutionRepository solutionRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private SolutionService solutionService;

    @Test
    @DisplayName("save solution succeeds")
    void saveSolution() {
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);

        SolutionRequest request = new SolutionRequest("code", 120, true, 10L);

        Solution savedSolution = Solution.builder()
                .code("code")
                .timeElapsed(120)
                .isSolved(true)
                .problemId(10L)
                .member(member)
                .build();
        ReflectionTestUtils.setField(savedSolution, "id", 100L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(solutionRepository.save(any(Solution.class))).thenReturn(savedSolution);

        SolutionResponse response = solutionService.save(request, 1L);

        ArgumentCaptor<Solution> captor = ArgumentCaptor.forClass(Solution.class);
        verify(solutionRepository).save(captor.capture());
        Solution captured = captor.getValue();
        assertThat(captured.getCode()).isEqualTo(request.getCode());
        assertThat(captured.getTimeElapsed()).isEqualTo(request.getTimeElapsed());
        assertThat(captured.isSolved()).isEqualTo(request.getSolved());
        assertThat(captured.getProblemId()).isEqualTo(request.getProblemId());
        assertThat(captured.getMember()).isEqualTo(member);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getProblemId()).isEqualTo(10L);
        assertThat(response.isSolved()).isTrue();
        assertThat(response.getMemberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("update solution succeeds")
    void updateSolution() {
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(1L);

        Solution solution = Solution.builder()
                .code("old")
                .timeElapsed(30)
                .isSolved(false)
                .problemId(5L)
                .member(owner)
                .build();
        ReflectionTestUtils.setField(solution, "id", 1L);

        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));

        SolutionRequest request = new SolutionRequest("new", 60, true, 7L);
        SolutionResponse response = solutionService.update(1L, request, 1L);

        assertThat(solution.getCode()).isEqualTo("new");
        assertThat(solution.getTimeElapsed()).isEqualTo(60);
        assertThat(solution.isSolved()).isTrue();
        assertThat(solution.getProblemId()).isEqualTo(7L);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getProblemId()).isEqualTo(7L);
    }

    @Test
    @DisplayName("update is denied for non-owner")
    void updateSolutionAccessDenied() {
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(1L);

        Solution solution = Solution.builder()
                .code("old")
                .timeElapsed(30)
                .isSolved(false)
                .problemId(5L)
                .member(owner)
                .build();

        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));

        SolutionRequest request = new SolutionRequest("new", 60, true, 7L);

        assertThatThrownBy(() -> solutionService.update(1L, request, 2L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("delete solution succeeds")
    void deleteSolution() {
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(1L);

        Solution solution = Solution.builder()
                .code("code")
                .timeElapsed(30)
                .isSolved(true)
                .problemId(5L)
                .member(owner)
                .build();

        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));

        solutionService.delete(1L, 1L);

        verify(solutionRepository).delete(solution);
    }

    @Test
    @DisplayName("delete is denied for non-owner")
    void deleteSolutionAccessDenied() {
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(1L);

        Solution solution = Solution.builder()
                .code("code")
                .timeElapsed(30)
                .isSolved(true)
                .problemId(5L)
                .member(owner)
                .build();

        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));

        assertThatThrownBy(() -> solutionService.delete(1L, 2L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("get member solutions succeeds")
    void getSolutionsByMember() {
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(3L);

        Solution solution = Solution.builder()
                .code("code")
                .timeElapsed(10)
                .isSolved(true)
                .problemId(2L)
                .member(member)
                .build();
        ReflectionTestUtils.setField(solution, "id", 99L);

        when(memberRepository.findById(3L)).thenReturn(Optional.of(member));
        when(solutionRepository.findAllByMemberIdOrderByCreatedAtDesc(3L)).thenReturn(List.of(solution));

        List<SolutionResponse> responses = solutionService.getSolutionsByMember(3L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(99L);
        assertThat(responses.get(0).getMemberId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("get member solutions raises not found when member does not exist")
    void getSolutionsByMemberNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> solutionService.getSolutionsByMember(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("get solution detail succeeds for owner")
    void getSolutionById() {
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(1L);

        Solution solution = Solution.builder()
                .code("code")
                .timeElapsed(10)
                .isSolved(true)
                .problemId(2L)
                .member(owner)
                .build();
        ReflectionTestUtils.setField(solution, "id", 7L);

        when(solutionRepository.findById(7L)).thenReturn(Optional.of(solution));

        SolutionResponse response = solutionService.getSolution(7L, 1L);

        assertThat(response.getId()).isEqualTo(7L);
        assertThat(response.getMemberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("get solution detail is denied for non-owner")
    void getSolutionByIdAccessDenied() {
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(1L);

        Solution solution = Solution.builder()
                .code("code")
                .timeElapsed(10)
                .isSolved(true)
                .problemId(2L)
                .member(owner)
                .build();
        ReflectionTestUtils.setField(solution, "id", 7L);

        when(solutionRepository.findById(7L)).thenReturn(Optional.of(solution));

        assertThatThrownBy(() -> solutionService.getSolution(7L, 99L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("missing solution raises not found")
    void findSolutionNotFound() {
        when(solutionRepository.findById(1L)).thenReturn(Optional.empty());

        SolutionRequest request = new SolutionRequest("code", 10, true, 2L);

        assertThatThrownBy(() -> solutionService.update(1L, request, 1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
