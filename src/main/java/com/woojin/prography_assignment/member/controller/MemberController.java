package com.woojin.prography_assignment.member.controller;

import com.woojin.prography_assignment.common.dto.ApiResponse;
import com.woojin.prography_assignment.member.dto.request.MemberUpdateRequest;
import com.woojin.prography_assignment.member.dto.response.MemberDeleteResponse;
import com.woojin.prography_assignment.member.dto.response.MemberDetailResponse;
import com.woojin.prography_assignment.member.dto.response.MemberResponse;
import com.woojin.prography_assignment.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMember(@PathVariable Long id) {

        MemberResponse response = memberService.getMember(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/admin/members/{id}")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> getMemberDetail(@PathVariable Long id) {

        MemberDetailResponse response = memberService.getMemberDetail(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @PatchMapping("/admin/members/{id}")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> updateMember(@PathVariable Long id,
                                                                          @RequestBody MemberUpdateRequest request) {
        MemberDetailResponse response = memberService.updateMember(id, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @DeleteMapping("/admin/members/{id}")
    public ResponseEntity<ApiResponse<MemberDeleteResponse>> deleteMember(@PathVariable Long id) {

        MemberDeleteResponse response = memberService.withdrawnMember(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }
}
