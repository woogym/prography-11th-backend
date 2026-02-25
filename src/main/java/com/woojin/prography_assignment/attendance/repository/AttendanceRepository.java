package com.woojin.prography_assignment.attendance.repository;

import com.woojin.prography_assignment.attendance.domain.Attendance;
import java.util.List;
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
}
