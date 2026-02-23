package com.woojin.prography_assignment.attendance.repository;

import com.woojin.prography_assignment.attendance.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
