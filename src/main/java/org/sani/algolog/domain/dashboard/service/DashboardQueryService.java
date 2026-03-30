package org.sani.algolog.domain.dashboard.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.sani.algolog.domain.dashboard.dto.DashboardCountResponse;
import org.sani.algolog.domain.dashboard.dto.DashboardSummaryResponse;
import org.sani.algolog.domain.dashboard.dto.HeatmapDayResponse;
import org.sani.algolog.domain.member.repository.MemberRepository;
import org.sani.algolog.query.dto.SummaryTotalsRow;
import org.sani.algolog.query.mapper.DashboardSummaryQueryMapper;
import org.sani.algolog.query.mapper.HeatmapQueryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardQueryService {

    private static final int HEATMAP_LOOKBACK_DAYS = 365;
    private static final ZoneId HEATMAP_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final DashboardSummaryQueryMapper dashboardSummaryQueryMapper;
    private final HeatmapQueryMapper heatmapQueryMapper;
    private final MemberRepository memberRepository;

    public List<HeatmapDayResponse> getHeatmap(Long memberId) {
        validateMember(memberId);

        LocalDate endDate = LocalDate.now(HEATMAP_ZONE_ID);
        LocalDate startDate = endDate.minusDays(HEATMAP_LOOKBACK_DAYS - 1L);

        return heatmapQueryMapper.findDailyCountsByMemberIdAndDateRange(
                        memberId,
                        startDate.atStartOfDay(),
                        endDate.atStartOfDay().plusDays(1)
                )
                .stream()
                .map(HeatmapDayResponse::from)
                .toList();
    }

    public DashboardSummaryResponse getSummary(Long memberId) {
        validateMember(memberId);

        SummaryTotalsRow totals = dashboardSummaryQueryMapper.findSummaryTotalsByMemberId(memberId);

        return DashboardSummaryResponse.of(
                totals == null ? SummaryTotalsRow.empty() : totals,
                dashboardSummaryQueryMapper.findPlatformCountsByMemberId(memberId).stream()
                        .map(DashboardCountResponse::from)
                        .toList(),
                dashboardSummaryQueryMapper.findDifficultyCountsByMemberId(memberId).stream()
                        .map(DashboardCountResponse::from)
                        .toList()
        );
    }

    private void validateMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new EntityNotFoundException("Member not found: " + memberId);
        }
    }
}
