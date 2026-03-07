package com.woojin.prography_assignment.session.service;

import com.woojin.prography_assignment.attendance.dto.AttendanceSummaryDto;
import com.woojin.prography_assignment.attendance.repository.AttendanceRepository;
import com.woojin.prography_assignment.session.dto.request.SessionUpdateRequest;
import com.woojin.prography_assignment.session.dto.response.SessionResponseForAdmin;
import com.woojin.prography_assignment.session.repository.SessionRepository;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.qr.domain.QrCode;
import com.woojin.prography_assignment.qr.repository.QrRepository;
import com.woojin.prography_assignment.session.domain.Session;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionUpdateService {

    private final SessionRepository sessionRepository;
    private final QrRepository qrRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public SessionResponseForAdmin sessionUpdate(Long id, SessionUpdateRequest request) {
        Session session = findSessionById(id);
        QrCode qrCode = findQrcodeBySessionId(id);
        session.update(
                request.title(),
                request.date(),
                request.time(),
                request.location()
        );
        session.updateStatus(request.status());

        AttendanceSummaryDto attendanceSummary = findAttendanceSummaryBySessionId(id);

        return SessionResponseForAdmin.from(
                session,
                attendanceSummary.toResponse(),
                qrCode.isActive());
    }

    @Transactional
    public SessionResponseForAdmin sessionCancelled(Long id) {
        Session session = findSessionById(id);
        session.cancel();
        QrCode qrCode = findQrcodeBySessionId(id);
        qrCode.expire();

        AttendanceSummaryDto attendanceSummary = findAttendanceSummaryBySessionId(id);

        return SessionResponseForAdmin.from(
                session,
                attendanceSummary.toResponse(),
                qrCode.isActive());
    }

    private Session findSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SESSION_NOT_FOUND,
                        ErrorCode.SESSION_NOT_FOUND.getMessage()));
    }

    private QrCode findQrcodeBySessionId(Long id) {
        return qrRepository.findBySessionId(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.QR_NOT_FOUND,
                        ErrorCode.QR_NOT_FOUND.getMessage()));
    }

    private AttendanceSummaryDto findAttendanceSummaryBySessionId(Long id) {
        return attendanceRepository.getAttendanceStatBySessionId(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVALID_INPUT,
                        "출석 집계에 실패했습니다."));
    }
}
