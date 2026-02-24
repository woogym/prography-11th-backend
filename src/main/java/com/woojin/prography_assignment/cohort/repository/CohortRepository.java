package com.woojin.prography_assignment.cohort.repository;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CohortRepository extends JpaRepository<Cohort, Long> {

    List<Cohort> findAllByOrderByGenerationAsc();
}
