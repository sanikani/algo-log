package org.sani.algolog.domain.dashboard.dto;

import org.sani.algolog.query.dto.SummaryTotalsRow;

import java.util.List;

public record DashboardSummaryResponse(
        long totalCount,
        long solvedCount,
        long failedCount,
        List<DashboardCountResponse> platformCounts,
        List<DashboardCountResponse> difficultyCounts
) {

    public static DashboardSummaryResponse of(
            SummaryTotalsRow totals,
            List<DashboardCountResponse> platformCounts,
            List<DashboardCountResponse> difficultyCounts
    ) {
        return new DashboardSummaryResponse(
                totals.totalCount(),
                totals.solvedCount(),
                totals.failedCount(),
                platformCounts,
                difficultyCounts
        );
    }
}
