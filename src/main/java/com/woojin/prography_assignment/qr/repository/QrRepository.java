package com.woojin.prography_assignment.qr.repository;

import com.woojin.prography_assignment.qr.domain.QrCode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QrRepository extends JpaRepository<QrCode, Long> {

    // 활성 qr코드가 있는 세션 id 목록 조회
    @Query("""
        SELECT DISTINCT qr.session.id
        FROM QrCode qr
        WHERE qr.session.id IN :sessionIds
        AND qr.expiresAt > :now
        """)
    List<Long> findSessionIdsWithActiveQr(@Param("sessionIds") List<Long> sessionIds,
                                          @Param("now") Instant now);

    @Query("""
        SELECT qr
        FROM QrCode qr
        WHERE qr.session.id = :sessionId
        AND qr.expiresAt > :now
        ORDER BY qr.createdAt DESC
        """)
    Optional<QrCode> findActiveQrCodeBySessionId(@Param("sessionId") Long sessionId,
                                                 @Param("now") Instant now);

    Optional<QrCode> findBySessionId(Long id);
}
