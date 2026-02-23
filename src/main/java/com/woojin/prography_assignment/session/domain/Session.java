package com.woojin.prography_assignment.session.domain;

import com.prography.attendance.cohort.domain.Cohort;
import com.prography.attendance.common.entity.BaseTimeEntity;
import com.woojin.prography_assignment.attendance.domain.AttendanceStatus;
import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.common.BaseTimeEntity;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.InvalidInputException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "sessions",
        indexes = {
                @Index(name = "idx_session_cohort", columnList = "cohort_id"),
                @Index(name = "idx_session_status", columnList = "status"),
                @Index(name = "idx_session_date", columnList = "date")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cohort_id", nullable = false, foreignKey = @ForeignKey(name = "fk_session_cohort"))
    private Cohort cohort;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatus status;

    private Session(
            Cohort cohort,
            String title,
            String description,
            LocalDate date,
            LocalTime time
    ) {
        validateCreation(cohort, title, date, time);

        this.cohort = cohort;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.status = SessionStatus.SCHEDULED;
    }

    public static Session create(
            Cohort cohort,
            String title,
            String description,
            LocalDate date,
            LocalTime time
    ) {
        return new Session(cohort, title, description, date, time);
    }

    public void update(String title, String description, LocalDate date, LocalTime time) {
        validateNotCancelled();
        validateTitle(title);
        validateDate(date);
        validateTime(time);

        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public void cancel() {
        if (this.status == SessionStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 일정입니다");
        }
        this.status = SessionStatus.CANCELLED;
    }

    /**
     * 일정 상태 변경
     */
    public void updateStatus(SessionStatus status) {
        validateNotCancelled();
        if (status == null) {
            throw new IllegalArgumentException("상태는 필수입니다");
        }
        this.status = status;
    }

    public AttendanceStatus determineAttendanceStatus(LocalDateTime checkTime) {
        LocalDateTime sessionStartTime = LocalDateTime.of(this.date, this.time);
        return checkTime.isAfter(sessionStartTime)
                ? AttendanceStatus.LATE
                : AttendanceStatus.PRESENT;
    }

    public int calculateLateMinutes(LocalDateTime checkTime) {
        LocalDateTime sessionStartTime = LocalDateTime.of(this.date, this.time);

        if (checkTime.isBefore(sessionStartTime) || checkTime.isEqual(sessionStartTime)) {
            return 0;
        }

        long minutes = java.time.Duration.between(sessionStartTime, checkTime).toMinutes();
        return (int) minutes;
    }

     // 진행중 상태 여부 확인
    public boolean isInProgress() {
        return this.status == SessionStatus.IN_PROGRESS;
    }

    // 취소 상태 여부 확인
    public boolean isCancelled() {
        return this.status == SessionStatus.CANCELLED;
    }

    private void validateCreation(Cohort cohort, String title, LocalDate date, LocalTime time) {
        if (cohort == null) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "기수 입력은 필수입니다.");
        }
        validateTitle(title);
        validateDate(date);
        validateTime(time);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "일정 제목은 필수입니다");
        }
        if (title.length() > 100) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "일정 제목은 100자를 초과할 수 없습니다");
        }
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "일정 날짜는 필수입니다");
        }
    }

    private void validateTime(LocalTime time) {
        if (time == null) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "일정 시간은 필수입니다");
        }
    }

    private void validateNotCancelled() {
        if (this.status == SessionStatus.CANCELLED) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "취소된 일정은 수정할 수 없습니다");
        }
    }
}