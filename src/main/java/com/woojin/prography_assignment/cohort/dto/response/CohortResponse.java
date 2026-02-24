package com.woojin.prography_assignment.cohort.dto.response;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import java.time.LocalDateTime;

public record CohortResponse(
        Long id,
        Integer generation,
        String name,
        LocalDateTime createdAt
) {

    public static CohortResponse from(Cohort cohort) {
        return new CohortResponse(
                cohort.getId(),
                cohort.getGeneration(),
                cohort.getName(),
                cohort.getCreatedAt()
        );
    }
}
