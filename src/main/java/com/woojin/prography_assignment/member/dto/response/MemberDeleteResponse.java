package com.woojin.prography_assignment.member.dto.response;

import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.domain.MemberStatus;
import java.time.Instant;
import java.time.LocalDateTime;

public record MemberDeleteResponse(
        Long id,
        String loginId,
        String name,
        MemberStatus status,
        Instant updatedAt
) {

    public static MemberDeleteResponse from(Member member) {
        return new MemberDeleteResponse(
                member.getId(),
                member.getLoginId(),
                member.getName(),
                member.getStatus(),
                member.getModifiedAt()
        );
    }
}
