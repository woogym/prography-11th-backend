package com.woojin.prography_assignment.session.controller;

import com.woojin.prography_assignment.session.dto.response.SessionResponseForMember;
import com.woojin.prography_assignment.common.dto.ApiResponse;
import com.woojin.prography_assignment.session.service.SessionRetrieveService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionRetrieveService sessionRetrieveService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SessionResponseForMember>>> getSessionsForMember() {

        List<SessionResponseForMember> response = sessionRetrieveService.getSessionsForMember();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
