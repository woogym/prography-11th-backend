package com.woojin.prography_assignment.member.dto.response;

import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.domain.MemberRole;
import com.woojin.prography_assignment.member.domain.MemberStatus;
import java.time.LocalDateTime;

public record LoginResponse(
        Long id,
        String loginId,
        String name,
        String phone,
        MemberStatus status,
        MemberRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static LoginResponse from(Member member) {
        return new LoginResponse(
                member.getId(),
                member.getLoginId(),
                member.getName(),
                member.getPhone(),
                member.getStatus(),
                member.getRole(),
                member.getCreatedAt(),
                member.getModifiedAt()
        );
    }
}
