package com.woojin.prography_assignment.member.controller;

import com.woojin.prography_assignment.common.dto.ApiResponse;
import com.woojin.prography_assignment.member.dto.CreateMemberRequest;
import com.woojin.prography_assignment.member.dto.MemberResponse;
import com.woojin.prography_assignment.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminMemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(
            @Valid @RequestBody CreateMemberRequest request
            ) {

        MemberResponse response = memberService.createMember(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}
