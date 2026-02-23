package com.woojin.prography_assignment.qr.domain;

import com.woojin.prography_assignment.common.BaseTimeEntity;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.InvalidInputException;
import com.woojin.prography_assignment.session.domain.Session;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QrCode extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qr_code_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false, foreignKey = @ForeignKey(name = "fk_qr_code_session"))
    private Session session;

    @Column(name = "hash_value", nullable = false, unique = true, length = 36)
    private String hashValue;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    private static final int EXPIRATION_HOURS = 24;

    private QrCode(Session session, LocalDateTime now) {
        validateCreation(session);

        this.session = session;
        this.hashValue = generateHashValue();
        this.expiresAt = now.plusHours(EXPIRATION_HOURS);
    }

    public static QrCode create(Session session, LocalDateTime now) {
        return new QrCode(session, now);
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(this.expiresAt);
    }

    public boolean isActive(LocalDateTime now) {
        return !isExpired(now);
    }

    public void expire(LocalDateTime now) {
        this.expiresAt = now;
    }

    private String generateHashValue() {
        return UUID.randomUUID().toString();
    }

    private void validateCreation(Session session) {
        if (session == null) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "일정은 필수입니다");
        }
    }
}
