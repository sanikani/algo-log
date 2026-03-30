package org.sani.algolog.domain.dashboard.dto;

import org.sani.algolog.query.dto.HeatmapDailyCountRow;

import java.time.LocalDate;

public record HeatmapDayResponse(
        LocalDate date,
        long solvedCount
) {

    public static HeatmapDayResponse from(HeatmapDailyCountRow row) {
        return new HeatmapDayResponse(row.solvedDate(), row.solvedCount());
    }
}
