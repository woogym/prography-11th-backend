package com.woojin.prography_assignment.attendance.dto;

import com.woojin.prography_assignment.session.dto.response.SessionResponse;

public record AttendanceSummaryResponse(
        Long present,
        Long absent,
        Long late,
        Long excused,
        Long total
) {

    public static AttendanceSummaryResponse zero() {
        return new AttendanceSummaryResponse(0L, 0L, 0L, 0L, 0L);
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
