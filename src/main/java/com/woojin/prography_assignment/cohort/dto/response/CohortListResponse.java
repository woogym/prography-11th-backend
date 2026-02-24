package com.woojin.prography_assignment.cohort.dto.response;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import java.time.LocalDateTime;

public record CohortListResponse(
        Long id,
        Integer generation,
        String name,
        LocalDateTime createdAt
) {

    public static CohortListResponse from(Cohort cohort) {
        return new CohortListResponse(
                cohort.getId(),
                cohort.getGeneration(),
                cohort.getName(),
                cohort.getCreatedAt()
        );
    }
}
