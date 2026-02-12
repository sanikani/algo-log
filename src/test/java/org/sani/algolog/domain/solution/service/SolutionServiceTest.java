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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolutionServiceTest {

    @Mock
    private SolutionRepository solutionRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private SolutionService solutionService;

    @Test
    @DisplayName("풀이 등록 성공")
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
    @DisplayName("풀이 수정 성공")
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
    @DisplayName("작성자가 아닌 경우 수정 실패")
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
    @DisplayName("풀이 삭제 성공")
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
    @DisplayName("작성자가 아닌 경우 삭제 실패")
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
    @DisplayName("회원 ID로 풀이 목록 조회 성공")
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

        when(solutionRepository.findAllByMemberId(3L)).thenReturn(List.of(solution));

        List<SolutionResponse> responses = solutionService.getSolutionsByMember(3L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(99L);
        assertThat(responses.get(0).getMemberId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("풀이 ID가 없으면 예외")
    void findSolutionNotFound() {
        when(solutionRepository.findById(1L)).thenReturn(Optional.empty());

        SolutionRequest request = new SolutionRequest("code", 10, true, 2L);

        assertThatThrownBy(() -> solutionService.update(1L, request, 1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
