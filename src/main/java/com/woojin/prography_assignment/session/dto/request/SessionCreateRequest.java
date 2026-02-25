package com.woojin.prography_assignment.session.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record SessionCreateRequest(
        String title,
        LocalDate date,
        LocalTime time,
        String location
) {
}
