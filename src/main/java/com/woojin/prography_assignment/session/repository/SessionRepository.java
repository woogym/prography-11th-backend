package com.woojin.prography_assignment.session.repository;

import com.woojin.prography_assignment.session.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
