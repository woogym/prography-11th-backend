package com.woojin.prography_assignment.session.repository;

import com.woojin.prography_assignment.session.domain.Session;
import com.woojin.prography_assignment.session.domain.SessionStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("""
        SELECT s
        FROM Session s
        WHERE s.cohort.id = :cohortId
        AND (:dateFrom IS NULL OR s.date >= :dateFrom)
        AND (:dateTo IS NULL OR s.date <= :dateTo)
        AND (:status IS NULL OR s.status = :status)
        ORDER BY s.date DESC, s.time DESC
        """)
    List<Session> findSessionsWithFilters(
            @Param("cohortId") Long cohortId,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("status") SessionStatus status
    );
}
