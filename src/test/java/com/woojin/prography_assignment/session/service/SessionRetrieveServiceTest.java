package com.woojin.prography_assignment.session.service;

import com.woojin.prography_assignment.attendance.domain.AttendanceStatus;
import com.woojin.prography_assignment.attendance.repository.AttendanceRepository;
import com.woojin.prography_assignment.cohort.config.CohortProperties;
import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.qr.repository.QrRepository;
import com.woojin.prography_assignment.session.domain.Session;
import com.woojin.prography_assignment.session.domain.SessionStatus;
import com.woojin.prography_assignment.session.dto.response.SessionResponseForAdmin;
import com.woojin.prography_assignment.session.dto.response.SessionResponseForMember;
import com.woojin.prography_assignment.session.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionRetrieveService 단위 테스트")
class SessionRetrieveServiceTest {

    @InjectMocks
    private SessionRetrieveService sessionRetrieveService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private QrRepository qrRepository;

    @Mock
    private CohortRepository cohortRepository;

    @Mock
    private CohortProperties cohortProperties;

    private static final Long COHORT_ID = 2L;
    private static final Long SESSION_ID = 1L;

    private Cohort cohort;
    private Session session;

    @BeforeEach
    void setUp() {
        cohort = Cohort.create(11, "11기");
        ReflectionTestUtils.setField(cohort, "id", COHORT_ID);

        session = Session.create(
                cohort,
                "1주차 세션",
                "프로그라피 본관",
                LocalDate.of(2026, 3, 1),
                LocalTime.of(14, 0)
        );
        ReflectionTestUtils.setField(session, "id", SESSION_ID);
    }

    @Nested
    @DisplayName("관리자용 세션 조회")
    class GetSessionsForAdmin {

        @Test
        @DisplayName("성공 (출석 통계 + QR 활성 포함)")
        void success() {
            // Given
            Object[] stat1 = {SESSION_ID, AttendanceStatus.PRESENT, 10L};
            Object[] stat2 = {SESSION_ID, AttendanceStatus.ABSENT, 2L};
            Object[] stat3 = {SESSION_ID, AttendanceStatus.LATE, 3L};

            given(cohortProperties.getGeneration()).willReturn(11);
            given(cohortRepository.findByGeneration(11)).willReturn(Optional.of(cohort));
            given(sessionRepository.findSessionsWithFilters(eq(COHORT_ID), any(), any(), any()))
                    .willReturn(List.of(session));
            given(attendanceRepository.getAttendanceStatsBySessionIds(anyList()))
                    .willReturn(List.of(stat1, stat2, stat3));
            given(qrRepository.findSessionIdsWithActiveQr(anyList(), any(Instant.class)))
                    .willReturn(List.of(SESSION_ID));

            // When
            List<SessionResponseForAdmin> responses = sessionRetrieveService.getSessionsForAdmin(
                    null, null, null
            );

            // Then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).title()).isEqualTo("1주차 세션");
            assertThat(responses.get(0).qrActive()).isTrue();
        }

        @Test
        @DisplayName("빈 결과")
        void empty() {
            // Given
            given(cohortProperties.getGeneration()).willReturn(11);
            given(cohortRepository.findByGeneration(11)).willReturn(Optional.of(cohort));
            given(sessionRepository.findSessionsWithFilters(eq(COHORT_ID), any(), any(), any()))
                    .willReturn(List.of());

            // When
            List<SessionResponseForAdmin> responses = sessionRetrieveService.getSessionsForAdmin(
                    null, null, null
            );

            // Then
            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("현재 기수 없음")
        void cohortNotFound() {
            // Given
            given(cohortProperties.getGeneration()).willReturn(99);
            given(cohortRepository.findByGeneration(99)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> sessionRetrieveService.getSessionsForAdmin(null, null, null))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("회원용 세션 조회")
    class GetSessionsForMember {

        @Test
        @DisplayName("성공")
        void success() {
            // Given
            SessionResponseForMember response1 = new SessionResponseForMember(
                    1L,
                    "1주차 세션",
                    LocalDate.of(2026, 3, 1),
                    LocalTime.of(14, 0),
                    "프로그라피 본관",
                    SessionStatus.SCHEDULED,
                    Instant.now(),
                    Instant.now()
            );

            given(cohortProperties.getGeneration()).willReturn(11);
            given(cohortRepository.findByGeneration(11)).willReturn(Optional.of(cohort));
            given(sessionRepository.findSessionsSummariesByCohortId(COHORT_ID))
                    .willReturn(List.of(response1));

            // When
            List<SessionResponseForMember> responses = sessionRetrieveService.getSessionsForMember();

            // Then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).title()).isEqualTo("1주차 세션");
        }
    }
}