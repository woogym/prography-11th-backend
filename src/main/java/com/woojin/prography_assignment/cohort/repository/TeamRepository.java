package com.woojin.prography_assignment.cohort.repository;

import com.woojin.prography_assignment.cohort.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
