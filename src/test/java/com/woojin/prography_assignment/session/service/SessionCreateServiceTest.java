package com.woojin.prography_assignment.session.service;

import com.woojin.prography_assignment.cohort.config.CohortProperties;
import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.qr.domain.QrCode;
import com.woojin.prography_assignment.qr.repository.QrRepository;
import com.woojin.prography_assignment.session.domain.Session;
import com.woojin.prography_assignment.session.dto.request.SessionCreateRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionCreateService 단위 테스트")
class SessionCreateServiceTest {

    @InjectMocks
    private SessionCreateService sessionCreateService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private QrRepository qrRepository;

    @Mock
    private CohortRepository cohortRepository;

    @Mock
    private CohortProperties cohortProperties;

    private Cohort cohort;
    private Session session;
    private QrCode qrCode;

    @BeforeEach
    void setUp() {
        cohort = Cohort.create(11, "11기");
        session = Session.create(
                cohort,
                "1주차 세션",
                "프로그라피 본관",
                LocalDate.of(2026, 3, 1),
                LocalTime.of(14, 0)
        );
        qrCode = QrCode.create(session);
    }

    @Nested
    @DisplayName("세션 생성")
    class CreateSession {

        @Test
        @DisplayName("성공 (QR 자동 생성)")
        void success() {
            // Given
            SessionCreateRequest request = new SessionCreateRequest(
                    "1주차 세션",
                    LocalDate.of(2026, 3, 1),
                    LocalTime.of(14, 0),
                    "프로그라피 본관"
            );

            given(cohortProperties.getGeneration()).willReturn(11);
            given(cohortRepository.findByGeneration(11)).willReturn(Optional.of(cohort));
            given(sessionRepository.save(any(Session.class))).willReturn(session);
            given(qrRepository.save(any(QrCode.class))).willReturn(qrCode);

            // When
            SessionResponseForAdmin response = sessionCreateService.createSession(request);

            // Then
            assertThat(response.title()).isEqualTo("1주차 세션");
            assertThat(response.location()).isEqualTo("프로그라피 본관");
            assertThat(response.qrActive()).isTrue();

            then(sessionRepository).should().save(any(Session.class));
            then(qrRepository).should().save(any(QrCode.class));
        }

        @Test
        @DisplayName("존재하지 않는 현재 기수")
        void cohortNotFound() {
            // Given
            SessionCreateRequest request = new SessionCreateRequest(
                    "1주차 세션",
                    LocalDate.of(2026, 3, 1),
                    LocalTime.of(14, 0),
                    "프로그라피 본관"
            );

            given(cohortProperties.getGeneration()).willReturn(99);
            given(cohortRepository.findByGeneration(99)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> sessionCreateService.createSession(request))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}