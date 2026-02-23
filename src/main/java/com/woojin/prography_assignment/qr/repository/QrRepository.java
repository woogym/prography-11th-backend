package com.woojin.prography_assignment.qr.repository;

import com.woojin.prography_assignment.qr.domain.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrRepository extends JpaRepository<QrCode, Long> {
}
