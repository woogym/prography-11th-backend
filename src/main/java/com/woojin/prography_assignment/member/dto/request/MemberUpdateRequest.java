package com.woojin.prography_assignment.member.dto.request;

import jakarta.validation.constraints.Pattern;

public record MemberUpdateRequest(
        String name,
        String phone,
        Long cohortId,
        Long partId,
        Long teamId
) {
}
