package com.woojin.prography_assignment.cohort.repository;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CohortRepository extends JpaRepository<Cohort, Long> {
}
