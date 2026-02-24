package com.woojin.prography_assignment.member.controller;

import com.woojin.prography_assignment.common.dto.ApiResponse;
import com.woojin.prography_assignment.common.dto.PageResponse;
import com.woojin.prography_assignment.member.domain.MemberStatus;
import com.woojin.prography_assignment.member.dto.request.CreateMemberRequest;
import com.woojin.prography_assignment.member.dto.response.MemberDashboardResponse;
import com.woojin.prography_assignment.member.dto.response.MemberCreateResponse;
import com.woojin.prography_assignment.member.service.MemberCreateService;
import com.woojin.prography_assignment.member.service.MemberDashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminMemberController {

    private final MemberCreateService memberCreateService;
    private final MemberDashboardService memberDashboardService;

    @PostMapping("/members")
    public ResponseEntity<ApiResponse<MemberCreateResponse>> createMember(
            @Valid @RequestBody CreateMemberRequest request
    ) {

        MemberCreateResponse response = memberCreateService.createMember(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/members")
    public ResponseEntity<ApiResponse<PageResponse<MemberDashboardResponse>>> getDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) Integer generation,
            @RequestParam(required = false) String partName,
            @RequestParam(required = false) String teamName,
            @RequestParam(required = false) MemberStatus status
    ) {
        PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                page,
                size,
                searchType,
                searchValue,
                generation,
                partName,
                teamName,
                status
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
