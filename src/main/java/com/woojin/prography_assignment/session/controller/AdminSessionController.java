package com.woojin.prography_assignment.session.controller;

import com.woojin.prography_assignment.common.dto.ApiResponse;
import com.woojin.prography_assignment.session.domain.SessionStatus;
import com.woojin.prography_assignment.session.dto.request.SessionCreateRequest;
import com.woojin.prography_assignment.session.dto.response.SessionResponse;
import com.woojin.prography_assignment.session.service.SessionCreateService;
import com.woojin.prography_assignment.session.service.SessionRetrieveService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final SessionCreateService sessionCreateService;
    private final SessionRetrieveService sessionRetrieveService;

    @PostMapping
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(
            @Valid @RequestBody SessionCreateRequest request
    ) {
        SessionResponse response = sessionCreateService.createSession(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getSessions(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateTo,

            @RequestParam(required = false)
            SessionStatus status
    ) {
        List<SessionResponse> response = sessionRetrieveService.getSessionsForAdmin(dateFrom, dateTo, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}