package com.woojin.prography_assignment.cohort.controller;

import com.woojin.prography_assignment.cohort.dto.response.CohortResponse;
import com.woojin.prography_assignment.cohort.service.CohortService;
import com.woojin.prography_assignment.common.dto.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/cohorts")
public class CohortController {

    private final CohortService cohortService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<CohortResponse>>> getCohorts() {

        List<CohortResponse> responses = cohortService.getCohorts();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(responses));
    }
}
