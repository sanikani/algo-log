package org.sani.algolog.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sani.algolog.query.dto.SummaryCountRow;
import org.sani.algolog.query.dto.SummaryTotalsRow;

import java.util.List;

@Mapper
public interface DashboardSummaryQueryMapper {

    SummaryTotalsRow findSummaryTotalsByMemberId(@Param("memberId") Long memberId);

    List<SummaryCountRow> findPlatformCountsByMemberId(@Param("memberId") Long memberId);

    List<SummaryCountRow> findDifficultyCountsByMemberId(@Param("memberId") Long memberId);
}
