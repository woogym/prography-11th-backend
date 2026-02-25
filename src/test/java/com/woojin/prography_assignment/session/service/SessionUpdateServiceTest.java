package com.woojin.prography_assignment.session.service;

import com.woojin.prography_assignment.attendance.dto.AttendanceSummaryDto;
import com.woojin.prography_assignment.attendance.repository.AttendanceRepository;
import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.qr.domain.QrCode;
import com.woojin.prography_assignment.qr.repository.QrRepository;
import com.woojin.prography_assignment.session.domain.Session;
import com.woojin.prography_assignment.session.domain.SessionStatus;
import com.woojin.prography_assignment.session.dto.request.SessionUpdateRequest;
import com.woojin.prography_assignment.session.dto.response.SessionResponseForAdmin;
import com.woojin.prography_assignment.session.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionUpdateService 단위 테스트")
class SessionUpdateServiceTest {

    @InjectMocks
    private SessionUpdateService sessionUpdateService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private QrRepository qrRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    private static final Long SESSION_ID = 1L;

    private Session session;
    private QrCode qrCode;
    private AttendanceSummaryDto attendanceSummary;

    @BeforeEach
    void setUp() {
        Cohort cohort = Cohort.create(11, "11기");
        session = Session.create(
                cohort,
                "1주차 세션",
                "프로그라피 본관",
                LocalDate.of(2026, 3, 1),
                LocalTime.of(14, 0)
        );
        qrCode = QrCode.create(session);
        attendanceSummary = new AttendanceSummaryDto(10L, 2L, 3L, 1L, 16L);
    }

    @Nested
    @DisplayName("세션 수정")
    class UpdateSession {

        @Test
        @DisplayName("성공")
        void success() {
            // Given
            SessionUpdateRequest request = new SessionUpdateRequest(
                    "2주차 세션",
                    LocalDate.of(2026, 3, 8),
                    LocalTime.of(15, 0),
                    "새로운 장소",
                    SessionStatus.COMPLETED
            );

            given(sessionRepository.findById(SESSION_ID)).willReturn(Optional.of(session));
            given(qrRepository.findBySessionId(SESSION_ID)).willReturn(Optional.of(qrCode));
            given(attendanceRepository.getAttendanceStatBySessionId(SESSION_ID))
                    .willReturn(Optional.of(attendanceSummary));

            // When
            SessionResponseForAdmin response = sessionUpdateService.sessionUpdate(SESSION_ID, request);

            // Then
            assertThat(response.title()).isEqualTo("2주차 세션");
            assertThat(response.location()).isEqualTo("새로운 장소");
            assertThat(response.status()).isEqualTo(SessionStatus.COMPLETED);
        }

        @Test
        @DisplayName("존재하지 않는 세션")
        void sessionNotFound() {
            // Given
            SessionUpdateRequest request = new SessionUpdateRequest(
                    "2주차 세션",
                    LocalDate.of(2026, 3, 8),
                    LocalTime.of(15, 0),
                    "새로운 장소",
                    SessionStatus.COMPLETED
            );

            given(sessionRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> sessionUpdateService.sessionUpdate(999L, request))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("세션 취소")
    class CancelSession {

        @Test
        @DisplayName("성공 (QR 만료)")
        void success() {
            // Given
            given(sessionRepository.findById(SESSION_ID)).willReturn(Optional.of(session));
            given(qrRepository.findBySessionId(SESSION_ID)).willReturn(Optional.of(qrCode));
            given(attendanceRepository.getAttendanceStatBySessionId(SESSION_ID))
                    .willReturn(Optional.of(attendanceSummary));

            // When
            SessionResponseForAdmin response = sessionUpdateService.sessionCancelled(SESSION_ID);

            // Then
            assertThat(response.status()).isEqualTo(SessionStatus.CANCELLED);
            assertThat(response.qrActive()).isFalse();
        }
    }
}