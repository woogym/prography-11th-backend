package com.woojin.prography_assignment.member.dto.request;

public record LoginRequest(
        String loginId,
        String password
) {
}
