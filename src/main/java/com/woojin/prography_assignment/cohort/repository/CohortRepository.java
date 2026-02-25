package com.woojin.prography_assignment.cohort.repository;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CohortRepository extends JpaRepository<Cohort, Long> {

    List<Cohort> findAllByOrderByGenerationAsc();

    Optional<Cohort> findByGeneration(Integer generation);
}
