package com.woojin.prography_assignment.attendance.repository;

import com.woojin.prography_assignment.attendance.domain.Attendance;
import com.woojin.prography_assignment.attendance.dto.AttendanceSummaryDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 여러 세션의 출석 통계를 일괄 조회
    @Query("""
        SELECT a.session.id, a.status, COUNT(a)
        FROM Attendance a
        WHERE a.session.id IN :sessionIds
        GROUP BY a.session.id, a.status
        """)
    List<Object[]> getAttendanceStatsBySessionIds(@Param("sessionIds") List<Long> sessionIds);

    @Query("""
        SELECT new com.woojin.prography_assignment.attendance.dto.AttendanceSummaryDto(
            COUNT(case when a.status = 'PRESENT' then 1 end),
            COUNT(case when a.status = 'ABSENT' then 1 end),
            COUNT(case when a.status = 'LATE' then 1 end),
            COUNT(case when a.status = 'EXCUSED' then 1 end),
            count(a)
        )
        FROM Attendance a
        where a.session.id = :sessionId
        """)
    Optional<AttendanceSummaryDto> getAttendanceStatBySessionId(@Param("sessionId") Long sessionId);
}
