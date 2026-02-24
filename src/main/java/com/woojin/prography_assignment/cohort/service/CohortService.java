package com.woojin.prography_assignment.cohort.service;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.Team;
import com.woojin.prography_assignment.cohort.dto.response.CohortDetailResponse;
import com.woojin.prography_assignment.cohort.dto.response.CohortResponse;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import com.woojin.prography_assignment.cohort.repository.PartRepository;
import com.woojin.prography_assignment.cohort.repository.TeamRepository;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CohortService {

    private final CohortRepository cohortRepository;
    private final PartRepository partRepository;
    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public List<CohortResponse> getCohorts() {
        List<Cohort> cohorts = cohortRepository.findAllByOrderByGenerationAsc();

        return cohorts.stream()
                .map(CohortResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CohortDetailResponse getCohortDetail(Long cohortId) {
        Cohort cohort = findCohortById(cohortId);
        List<Part> parts = partRepository.findByCohortId(cohortId);
        List<Team> teams = teamRepository.findByCohortId(cohortId);

        return CohortDetailResponse.from(cohort, parts, teams);
    }

    private Cohort findCohortById(Long cohortId) {
        return cohortRepository.findById(cohortId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COHORT_NOT_FOUND,
                        ErrorCode.COHORT_NOT_FOUND.getMessage()));
    }
}
