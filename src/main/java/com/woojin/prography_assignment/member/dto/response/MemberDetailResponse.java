package com.woojin.prography_assignment.member.dto.response;

import com.woojin.prography_assignment.member.domain.CohortMember;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.Team;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.domain.MemberRole;
import com.woojin.prography_assignment.member.domain.MemberStatus;
import java.time.Instant;
import java.time.LocalDateTime;

public record MemberDetailResponse(
        Long id,
        String loginId,
        String name,
        String phone,
        MemberStatus status,
        MemberRole role,
        Integer generation,
        String partName,
        String teamName,
        Instant createdAt,
        Instant updatedAt
) {

    public static MemberDetailResponse from(Member member, CohortMember cohortMember) {
        return new MemberDetailResponse(
                member.getId(),
                member.getLoginId(),
                member.getName(),
                member.getPhone(),
                member.getStatus(),
                member.getRole(),
                cohortMember.getCohort().getGeneration(),
                getPartName(cohortMember.getPart()),
                getTeamName(cohortMember.getTeam()),
                member.getCreatedAt(),
                member.getModifiedAt()
        );
    }

    private static String getPartName(Part part) {
        if (part == null) {
            return "Part 없음";
        }

        return part.getPartType().name();
    }

    private static String getTeamName(Team team) {
        if (team == null) {
            return "Team 없음";
        }

        return team.getName();
    }
}
