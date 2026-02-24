package com.woojin.prography_assignment.member.controller;

import com.woojin.prography_assignment.common.dto.ApiResponse;
import com.woojin.prography_assignment.member.dto.request.LoginRequest;
import com.woojin.prography_assignment.member.dto.response.MemberResponse;
import com.woojin.prography_assignment.member.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberResponse>> login(@RequestBody LoginRequest request) {
        MemberResponse response = loginService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }
}
