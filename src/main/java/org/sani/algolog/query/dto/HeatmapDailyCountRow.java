package org.sani.algolog.query.dto;

import java.time.LocalDate;

public record HeatmapDailyCountRow(
        LocalDate solvedDate,
        long solvedCount
) {
}
