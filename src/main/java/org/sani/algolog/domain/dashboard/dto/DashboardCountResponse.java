package org.sani.algolog.domain.dashboard.dto;

import org.sani.algolog.query.dto.SummaryCountRow;

public record DashboardCountResponse(
        String name,
        long count
) {

    public static DashboardCountResponse from(SummaryCountRow row) {
        return new DashboardCountResponse(row.name(), row.count());
    }
}
