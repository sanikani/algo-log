package org.sani.algolog.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sani.algolog.query.dto.HeatmapDailyCountRow;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HeatmapQueryMapper {

    List<HeatmapDailyCountRow> findDailyCountsByMemberIdAndDateRange(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
