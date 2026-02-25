package com.woojin.prography_assignment.cohort.dto.response;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.Team;
import java.time.Instant;
import java.util.List;

public record CohortDetailResponse(
        Long id,
        Integer generation,
        String name,
        List<PartSummary> parts,
        List<TeamSummary> teams,
        Instant createdAt
) {

    public static CohortDetailResponse from(Cohort cohort, List<Part> parts, List<Team> teams) {
        return new CohortDetailResponse(
                cohort.getId(),
                cohort.getGeneration(),
                cohort.getName(),
                parts.stream().map(PartSummary::from).toList(),
                teams.stream().map(TeamSummary::from).toList(),
                cohort.getCreatedAt()
        );
    }

    public record PartSummary(Long id, String name) {
        public static PartSummary from(Part part) {
            return new PartSummary(
                    part.getId(),
                    part.getPartType().name()
            );
        }
    }

    public record TeamSummary(Long id, String name) {
        public static TeamSummary from(Team team) {
            return new TeamSummary(
                    team.getId(),
                    team.getName()
            );
        }
    }
}
