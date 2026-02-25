package com.woojin.prography_assignment.qr.dto.response;

import com.woojin.prography_assignment.qr.domain.QrCode;
import com.woojin.prography_assignment.session.domain.Session;
import java.time.Instant;

public record QrResponse(
        Long id,
        Long sessionId,
        String hashValue,
        Instant createdAt,
        Instant expiredAt
) {

    public static QrResponse from(QrCode qrCode, Session session) {
        return new QrResponse(
                qrCode.getId(),
                session.getId(),
                qrCode.getHashValue(),
                qrCode.getCreatedAt(),
                qrCode.getExpiresAt()
        );
    }
}
