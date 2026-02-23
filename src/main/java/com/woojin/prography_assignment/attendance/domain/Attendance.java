package com.woojin.prography_assignment.attendance.domain;

import com.woojin.prography_assignment.cohort.domain.CohortMember;
import com.woojin.prography_assignment.common.BaseTimeEntity;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.InvalidInputException;
import com.woojin.prography_assignment.qr.domain.QRCode;
import com.woojin.prography_assignment.session.domain.Session;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "attendances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attendance_session_cohort_member",
                        columnNames = {"session_id", "cohort_member_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cohort_member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attendance_cohort_member"))
    private CohortMember cohortMember;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attendance_session"))
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qr_code_id", foreignKey = @ForeignKey(name = "fk_attendance_qr_code"))
    private QRCode qrCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    @Column(name = "penalty_amount", nullable = false)
    private Integer penaltyAmount;

    @Column(name = "reason", length = 200)
    private String reason;

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    private Attendance(
            CohortMember cohortMember,
            Session session,
            QRCode qrCode,
            AttendanceStatus status,
            Integer lateMinutes,
            Integer penaltyAmount,
            String reason,
            LocalDateTime checkedInAt
    ) {
        validateCreation(cohortMember, session, status, penaltyAmount);

        this.cohortMember = cohortMember;
        this.session = session;
        this.qrCode = qrCode;
        this.status = status;
        this.lateMinutes = lateMinutes;
        this.penaltyAmount = penaltyAmount;
        this.reason = reason;
        this.checkedInAt = checkedInAt;
    }

    public static Attendance createByQR(
            CohortMember cohortMember,
            Session session,
            QRCode qrCode,
            AttendanceStatus status,
            Integer lateMinutes,
            Integer penaltyAmount,
            LocalDateTime checkedInAt
    ) {
        return new Attendance(
                cohortMember,
                session,
                qrCode,
                status,
                lateMinutes,
                penaltyAmount,
                null,  // QR 체크인은 사유 없음
                checkedInAt
        );
    }

    public static Attendance createManually(
            CohortMember cohortMember,
            Session session,
            AttendanceStatus status,
            Integer lateMinutes,
            Integer penaltyAmount,
            String reason
    ) {
        return new Attendance(
                cohortMember,
                session,
                null,  // 수동 등록은 QR 없음
                status,
                lateMinutes,
                penaltyAmount,
                reason,
                null   // 수동 등록은 체크인 시각 없음
        );
    }

    public void updateStatus(
            AttendanceStatus newStatus,
            Integer newLateMinutes,
            Integer newPenaltyAmount,
            String newReason
    ) {
        validateStatusUpdate(newStatus, newPenaltyAmount);

        this.status = newStatus;
        this.lateMinutes = newLateMinutes;
        this.penaltyAmount = newPenaltyAmount;
        this.reason = newReason;
    }

    public int calculatePenaltyDifference(int newPenaltyAmount) {
        return newPenaltyAmount - this.penaltyAmount;
    }

    private void validateCreation(
            CohortMember cohortMember,
            Session session,
            AttendanceStatus status,
            Integer penaltyAmount
    ) {
        if (cohortMember == null) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "기수 회원은 필수입니다");
        }
        if (session == null) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "일정은 필수입니다");
        }
        if (status == null) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "출결 상태는 필수입니다");
        }
        if (penaltyAmount == null || penaltyAmount < 0) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "패널티 금액은 0 이상이어야 합니다");
        }
    }

    private void validateStatusUpdate(AttendanceStatus status, Integer penaltyAmount) {
        if (status == null) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "출결 상태는 필수입니다");
        }
        if (penaltyAmount == null || penaltyAmount < 0) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "패널티 금액은 0 이상이어야 합니다");
        }
    }
}
