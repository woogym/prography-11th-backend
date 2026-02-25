package com.woojin.prography_assignment.session.dto.response;

import com.woojin.prography_assignment.session.domain.Session;
import com.woojin.prography_assignment.session.domain.SessionStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record SessionResponseForMember(
        Long id,
        String title,
        LocalDate date,
        LocalTime time,
        String location,
        SessionStatus status,
        Instant createdAt,
        Instant updateAt
) {

    public static SessionResponseForMember from(Session session) {
        return new SessionResponseForMember(
                session.getId(),
                session.getTitle(),
                session.getDate(),
                session.getTime(),
                session.getLocation(),
                session.getStatus(),
                session.getCreatedAt(),
                session.getModifiedAt()
        );
    }
}
