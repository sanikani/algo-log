package org.sani.algolog.query.dto;

public record SummaryTotalsRow(
        long totalCount,
        long solvedCount,
        long failedCount
) {

    public static SummaryTotalsRow empty() {
        return new SummaryTotalsRow(0L, 0L, 0L);
    }
}
