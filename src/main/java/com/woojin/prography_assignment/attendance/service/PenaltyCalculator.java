package com.woojin.prography_assignment.attendance.service;

import com.woojin.prography_assignment.attendance.domain.AttendanceStatus;
import org.springframework.stereotype.Component;

@Component
public class PenaltyCalculator {

    private static final int ABSENT_PENALTY = 10_000;
    private static final int LATE_PENALTY_PER_MINUTE = 500;
    private static final int MAX_LATE_PENALTY = 10_000;

    public int calculate(AttendanceStatus attendanceStatus, Integer lateMinute) {
        return switch (attendanceStatus) {
            case PRESENT, EXCUSED -> 0;
            case ABSENT -> ABSENT_PENALTY;
            case LATE -> calculateLatePenalty(lateMinute);
        };
    }

    private int calculateLatePenalty(Integer lateMinute) {
        if (lateMinute == null || lateMinute == 0) {
            return 0;
        }

        int penalty = lateMinute * LATE_PENALTY_PER_MINUTE;
        return Math.min(penalty, MAX_LATE_PENALTY);
    }
}
