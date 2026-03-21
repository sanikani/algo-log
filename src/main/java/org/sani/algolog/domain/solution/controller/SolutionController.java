package org.sani.algolog.domain.solution.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sani.algolog.domain.solution.dto.SolutionCreateRequest;
import org.sani.algolog.domain.solution.dto.SolutionResponse;
import org.sani.algolog.domain.solution.service.SolutionService;
import org.sani.algolog.global.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Solution", description = "Solution APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/solutions")
public class SolutionController {

    private static final String MEMBER_ID_HEADER = "X-Member-Id";

    private final SolutionService solutionService;

    @Operation(summary = "Save solution", description = "Saves a solution for the member in X-Member-Id header.")
    @PostMapping
    public ApiResponse<SolutionResponse> save(
            @RequestHeader(MEMBER_ID_HEADER) Long memberId,
            @Valid @RequestBody SolutionCreateRequest request
    ) {
        return ApiResponse.success(solutionService.save(request, memberId));
    }

    @Operation(summary = "Get my solutions", description = "Returns all solutions owned by the member in latest-first order.")
    @GetMapping
    public ApiResponse<List<SolutionResponse>> getSolutions(
            @RequestHeader(MEMBER_ID_HEADER) Long memberId
    ) {
        return ApiResponse.success(solutionService.getSolutionsByMember(memberId));
    }

    @Operation(summary = "Get solution detail", description = "Returns a single solution only when owned by the member.")
    @GetMapping("/{id}")
    public ApiResponse<SolutionResponse> getSolution(
            @PathVariable Long id,
            @RequestHeader(MEMBER_ID_HEADER) Long memberId
    ) {
        return ApiResponse.success(solutionService.getSolution(id, memberId));
    }
}
