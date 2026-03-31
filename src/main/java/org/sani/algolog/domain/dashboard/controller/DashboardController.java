package org.sani.algolog.domain.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sani.algolog.domain.dashboard.dto.DashboardSummaryResponse;
import org.sani.algolog.domain.dashboard.dto.HeatmapDayResponse;
import org.sani.algolog.domain.dashboard.service.DashboardQueryService;
import org.sani.algolog.global.common.ApiResponse;
import org.sani.algolog.security.resolver.CurrentMemberId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Dashboard", description = "Dashboard query APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardQueryService dashboardQueryService;

    @Operation(
            summary = "Get heatmap data",
            description = "Returns daily solution counts for the currently authenticated member."
    )
    @GetMapping("/heatmap")
    public ApiResponse<List<HeatmapDayResponse>> getHeatmap(
            @CurrentMemberId Long memberId
    ) {
        return ApiResponse.success(dashboardQueryService.getHeatmap(memberId));
    }

    @Operation(
            summary = "Get dashboard summary data",
            description = "Returns summary counts grouped for the currently authenticated member."
    )
    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> getSummary(
            @CurrentMemberId Long memberId
    ) {
        return ApiResponse.success(dashboardQueryService.getSummary(memberId));
    }
}
