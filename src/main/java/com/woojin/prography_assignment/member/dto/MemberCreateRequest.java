package com.woojin.prography_assignment.member.dto;

import com.woojin.prography_assignment.member.domain.Member;

public record MemberCreateRequest(
        String loginId,
        String password,
        String name,
        String phone,
        Long cohortId,
        Long partId,
        Long teamId
) {

}
