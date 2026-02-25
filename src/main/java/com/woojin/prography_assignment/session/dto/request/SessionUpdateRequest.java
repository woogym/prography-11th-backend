package com.woojin.prography_assignment.session.dto.request;

import com.woojin.prography_assignment.session.domain.SessionStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record SessionUpdateRequest(
        String title,
        LocalDate date,
        LocalTime time,
        String location,
        SessionStatus status
) {
}
