package com.woojin.prography_assignment.attendance.dto;

import com.woojin.prography_assignment.session.dto.response.SessionResponse;

public record AttendanceSummaryDto(
        Long present,
        Long absent,
        Long late,
        Long excused,
        Long total
) {

    public static AttendanceSummaryDto zero() {
        return new AttendanceSummaryDto(0L, 0L, 0L, 0L, 0L);
    }

    public SessionResponse.AttendanceSummary toResponse() {
        return new SessionResponse.AttendanceSummary(
                present.intValue(),
                absent.intValue(),
                late.intValue(),
                excused.intValue(),
                total.intValue()
        );
    }
}
