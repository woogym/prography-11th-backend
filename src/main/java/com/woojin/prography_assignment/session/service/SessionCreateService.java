package com.woojin.prography_assignment.session.service;

import com.woojin.prography_assignment.cohort.config.CohortProperties;
import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.qr.domain.QrCode;
import com.woojin.prography_assignment.qr.repository.QrRepository;
import com.woojin.prography_assignment.session.domain.Session;
import com.woojin.prography_assignment.session.dto.request.SessionCreateRequest;
import com.woojin.prography_assignment.session.dto.response.SessionResponse;
import com.woojin.prography_assignment.session.repository.SessionRepository;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionCreateService {

    private final SessionRepository sessionRepository;
    private final QrRepository qrRepository;
    private final CohortRepository cohortRepository;
    private final CohortProperties cohortProperties;

    @Transactional
    public SessionResponse createSession(SessionCreateRequest request) {
        Cohort cohort = findCurrentCohort();

        Session session = Session.create(
                cohort,
                request.title(),
                request.location(),
                request.date(),
                request.time()
        );
        sessionRepository.save(session);

        QrCode qrCode = QrCode.create(session, Instant.now());
        qrRepository.save(qrCode);

        return SessionResponse.from(session, true);
    }

    private Cohort findCurrentCohort() {
        Integer generation = cohortProperties.getGeneration();

        return cohortRepository.findByGeneration(generation)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVALID_INPUT,
                        "존재하지 않는 기수입니다."));
    }
}
