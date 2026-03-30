package org.sani.algolog.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sani.algolog.query.dto.HeatmapDailyCountRow;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface HeatmapQueryMapper {

    List<HeatmapDailyCountRow> findDailyCountsByMemberIdAndDateRange(
            @Param("memberId") Long memberId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endExclusiveDateTime") LocalDateTime endExclusiveDateTime
    );
}
