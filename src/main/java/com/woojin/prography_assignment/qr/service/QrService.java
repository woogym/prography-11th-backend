package com.woojin.prography_assignment.qr.service;

import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.ActiveQRAlreadyExistsException;
import com.woojin.prography_assignment.common.exception.model.BusinessException;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.qr.domain.QrCode;
import com.woojin.prography_assignment.qr.dto.response.QrResponse;
import com.woojin.prography_assignment.qr.repository.QrRepository;
import com.woojin.prography_assignment.session.domain.Session;
import com.woojin.prography_assignment.session.repository.SessionRepository;
import java.time.Instant;
import java.util.Optional;
import javax.swing.text.html.Option;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class QrService {

    private final QrRepository qrRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public QrResponse createQrcode(Long sessionId) {
        Session session = findSessionById(sessionId);
        validateNoActiveQrCode(sessionId);

        QrCode qrCode = QrCode.create(session);
        qrRepository.save(qrCode);

        return QrResponse.from(qrCode, session);
    }

    private Session findSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SESSION_NOT_FOUND,
                        ErrorCode.SESSION_NOT_FOUND.getMessage()));
    }

    public void validateNoActiveQrCode(Long sessionId) {
        Optional<QrCode> activeQr = qrRepository
                .findActiveQrCodeBySessionId(sessionId, Instant.now());

        if (activeQr.isPresent()) {
            throw new ActiveQRAlreadyExistsException();
        }
    }
}
