package org.sani.algolog.domain.dashboard.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.sani.algolog.domain.dashboard.dto.HeatmapDayResponse;
import org.sani.algolog.domain.member.repository.MemberRepository;
import org.sani.algolog.query.mapper.HeatmapQueryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardQueryService {

    private static final int HEATMAP_LOOKBACK_DAYS = 365;

    private final HeatmapQueryMapper heatmapQueryMapper;
    private final MemberRepository memberRepository;

    public List<HeatmapDayResponse> getHeatmap(Long memberId) {
        validateMember(memberId);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(HEATMAP_LOOKBACK_DAYS - 1L);

        return heatmapQueryMapper.findDailyCountsByMemberIdAndDateRange(memberId, startDate, endDate)
                .stream()
                .map(HeatmapDayResponse::from)
                .toList();
    }

    private void validateMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new EntityNotFoundException("Member not found: " + memberId);
        }
    }
}
