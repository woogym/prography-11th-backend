package com.woojin.prography_assignment.session.dto.response;

import com.woojin.prography_assignment.session.domain.Session;
import com.woojin.prography_assignment.session.domain.SessionStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record SessionResponseForAdmin(
        Long id,
        Long cohortId,
        String title,
        LocalDate date,
        LocalTime time,
        String location,
        SessionStatus status,
        AttendanceSummary attendanceSummary,
        Boolean qrActive,
        Instant createdAt,
        Instant updatedAt
) {

    public static SessionResponseForAdmin from(Session session, boolean qrActive) {
        return new SessionResponseForAdmin(
                session.getId(),
                session.getCohort().getId(),
                session.getTitle(),
                session.getDate(),
                session.getTime(),
                session.getLocation(),
                session.getStatus(),
                AttendanceSummary.zero(),
                qrActive,
                session.getCreatedAt(),
                session.getModifiedAt()
        );
    }

    public static SessionResponseForAdmin from(Session session, AttendanceSummary summary, boolean qrActive) {
        return new SessionResponseForAdmin(
                session.getId(),
                session.getCohort().getId(),
                session.getTitle(),
                session.getDate(),
                session.getTime(),
                session.getLocation(),
                session.getStatus(),
                summary,
                qrActive,
                session.getCreatedAt(),
                session.getModifiedAt()
        );
    }

    public record AttendanceSummary(
            Integer present,
            Integer absent,
            Integer late,
            Integer excused,
            Integer total
    ) {
        public static AttendanceSummary zero() {
            return new AttendanceSummary(0, 0, 0, 0, 0);
        }
    }
}