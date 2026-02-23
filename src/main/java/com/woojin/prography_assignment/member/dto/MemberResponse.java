package com.woojin.prography_assignment.member.dto;

import com.woojin.prography_assignment.cohort.domain.CohortMember;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.Team;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.domain.MemberRole;
import com.woojin.prography_assignment.member.domain.MemberStatus;
import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        String loginId,
        String name,
        String phone,
        MemberStatus memberStatus,
        MemberRole memberRole,
        Integer generation,
        String partName,
        String teamName,
        LocalDateTime createdAt,
        LocalDateTime updateAt
) {

    public static MemberResponse from(Member member, CohortMember cohortMember) {
        return new MemberResponse(
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
            return "정해진 Part 없음";
        }

        return part.getPartType().name();
    }

    private static String getTeamName(Team team) {
        if (team == null) {
            return "정해진 Team 없음";
        }

        return team.getName();
    }
}
