package com.woojin.prography_assignment.session.service;


import com.woojin.prography_assignment.attendance.domain.AttendanceStatus;
import com.woojin.prography_assignment.attendance.dto.AttendanceSummaryDto;
import com.woojin.prography_assignment.attendance.repository.AttendanceRepository;
import com.woojin.prography_assignment.cohort.config.CohortProperties;
import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.qr.repository.QrRepository;
import com.woojin.prography_assignment.session.domain.Session;
import com.woojin.prography_assignment.session.domain.SessionStatus;
import com.woojin.prography_assignment.session.dto.response.SessionResponse;
import com.woojin.prography_assignment.session.repository.SessionRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionRetrieveService {

    private final SessionRepository sessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final QrRepository qrRepository;
    private final CohortRepository cohortRepository;
    private final CohortProperties cohortProperties;

    public List<SessionResponse> getSessionsForAdmin(LocalDate dateFrom,
                                                     LocalDate dateTo,
                                                     SessionStatus status) {

        Cohort currentCohort = findCurrentCohort();
        List<Session> sessions = sessionRepository.findSessionsWithFilters(
                currentCohort.getId(),
                dateFrom,
                dateTo,
                status
        );

        if (sessions.isEmpty()) {
            return List.of();
        }

        List<Long> sessionIds = sessions.stream()
                .map(Session::getId)
                .toList();
        Map<Long, AttendanceSummaryDto> attendanceSummaries = getAttendanceSummaries(sessionIds);
        Set<Long> activeQrSessionsId = getActiveQrSessionIds(sessionIds);

        return sessions.stream()
                .map(session -> toResponse(session, attendanceSummaries, activeQrSessionsId))
                .toList();
    }

    private SessionResponse toResponse(
            Session session,
            Map<Long, AttendanceSummaryDto> summaryMap,
            Set<Long> activeQrSessionIds
    ) {
        AttendanceSummaryDto summary = summaryMap.getOrDefault(
                session.getId(),
                AttendanceSummaryDto.zero()
        );

        boolean qrActive = activeQrSessionIds.contains(session.getId());

        return SessionResponse.from(session, summary.toResponse(), qrActive);
    }

    private Map<Long, AttendanceSummaryDto> getAttendanceSummaries(List<Long> sessionIds) {
        List<Object[]> stats = attendanceRepository.getAttendanceStatsBySessionIds(sessionIds);

        Map<Long, List<Object[]>> groupedBySession = stats.stream()
                .collect(Collectors.groupingBy(row -> (Long) row[0]));

        return groupedBySession.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> aggregateSummary(entry.getValue())
                ));
    }

    private AttendanceSummaryDto aggregateSummary(List<Object[]> rows) {
        long present = 0;
        long absent = 0;
        long late = 0;
        long excused = 0;

        for (Object[] row : rows) {
            AttendanceStatus status = (AttendanceStatus) row[1];
            Long count = (Long) row[2];

            switch (status) {
                case PRESENT -> present = count;
                case ABSENT -> absent = count;
                case LATE -> late = count;
                case EXCUSED -> excused = count;
            }
        }

        long total = present + absent + late + excused;
        return new AttendanceSummaryDto(present, absent, late, excused, total);
    }

    private Set<Long> getActiveQrSessionIds(List<Long> sessionIds) {
        return new HashSet<>(qrRepository.findSessionIdsWithActiveQr(sessionIds, Instant.now()));
    }

    private Cohort findCurrentCohort() {
        Integer currentGeneration = cohortProperties.getGeneration();
        return cohortRepository.findByGeneration(currentGeneration)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COHORT_NOT_FOUND));
    }
}
