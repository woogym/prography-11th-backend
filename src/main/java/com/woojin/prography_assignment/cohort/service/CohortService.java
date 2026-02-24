package com.woojin.prography_assignment.cohort.service;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.dto.response.CohortResponse;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CohortService {

    private final CohortRepository cohortRepository;

    @Transactional(readOnly = true)
    public List<CohortResponse> getCohorts() {
        List<Cohort> cohorts = cohortRepository.findAllByOrderByGenerationAsc();

        return cohorts.stream()
                .map(CohortResponse::from)
                .toList();
    }
}
