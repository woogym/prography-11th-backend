package com.woojin.prography_assignment.qr.service;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.common.exception.model.ActiveQRAlreadyExistsException;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.qr.domain.QrCode;
import com.woojin.prography_assignment.qr.dto.response.QrResponse;
import com.woojin.prography_assignment.qr.repository.QrRepository;
import com.woojin.prography_assignment.session.domain.Session;
import com.woojin.prography_assignment.session.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("QrService 단위 테스트")
class QrServiceTest {

    @InjectMocks
    private QrService qrService;

    @Mock
    private QrRepository qrRepository;

    @Mock
    private SessionRepository sessionRepository;

    private static final Long SESSION_ID = 1L;
    private static final Long QR_ID = 1L;

    private Session session;
    private QrCode qrCode;

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
        ReflectionTestUtils.setField(session, "id", SESSION_ID);

        qrCode = QrCode.create(session);
        ReflectionTestUtils.setField(qrCode, "id", QR_ID);
    }

    @Nested
    @DisplayName("QR 코드 생성")
    class CreateQrCode {

        @Test
        @DisplayName("성공")
        void success() {
            // Given
            ArgumentCaptor<QrCode> qrCodeCaptor = ArgumentCaptor.forClass(QrCode.class);

            given(sessionRepository.findById(SESSION_ID)).willReturn(Optional.of(session));
            given(qrRepository.findActiveQrCodeBySessionId(any(Long.class), any(Instant.class)))
                    .willReturn(Optional.empty());
            given(qrRepository.save(qrCodeCaptor.capture())).willReturn(qrCode);

            // When
            QrResponse response = qrService.createQrcode(SESSION_ID);

            // Then
            QrCode savedQrCode = qrCodeCaptor.getValue();
            assertThat(response.hashValue()).isEqualTo(savedQrCode.getHashValue());
            assertThat(response.sessionId()).isEqualTo(SESSION_ID);
        }


        @Test
        @DisplayName("활성 QR 이미 존재")
        void activeQrExists() {
            // Given
            given(sessionRepository.findById(SESSION_ID)).willReturn(Optional.of(session));
            given(qrRepository.findActiveQrCodeBySessionId(any(Long.class), any(Instant.class)))
                    .willReturn(Optional.of(qrCode));

            // When & Then
            assertThatThrownBy(() -> qrService.createQrcode(SESSION_ID))
                    .isInstanceOf(ActiveQRAlreadyExistsException.class);
        }

        @Test
        @DisplayName("존재하지 않는 세션")
        void sessionNotFound() {
            // Given
            given(sessionRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> qrService.createQrcode(999L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("QR 코드 갱신")
    class RenewQrCode {

        @Test
        @DisplayName("성공")
        void success() {
            // Given
            given(qrRepository.findById(QR_ID)).willReturn(Optional.of(qrCode));
            given(sessionRepository.findSessionByQrCodeId(QR_ID)).willReturn(Optional.of(session));

            // When
            QrResponse response = qrService.renewQrcode(QR_ID);

            // Then
            assertThat(response.id()).isEqualTo(QR_ID);
            assertThat(response.sessionId()).isEqualTo(SESSION_ID);
        }

        @Test
        @DisplayName("존재하지 않는 QR 코드")
        void qrNotFound() {
            // Given
            given(qrRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> qrService.renewQrcode(999L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}