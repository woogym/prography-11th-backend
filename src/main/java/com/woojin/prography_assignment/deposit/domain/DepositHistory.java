package com.woojin.prography_assignment.deposit.domain;

import com.woojin.prography_assignment.attendance.domain.Attendance;
import com.woojin.prography_assignment.cohort.domain.CohortMember;
import com.woojin.prography_assignment.common.BaseTimeEntity;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cohort_member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_deposit_history_cohort_member"))
    private CohortMember cohortMember;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private DepositType type;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", foreignKey = @ForeignKey(name = "fk_deposit_history_attendance"))
    private Attendance attendance;

    @Column(name = "description", length = 200)
    private String description;

    private DepositHistory(
            CohortMember cohortMember,
            DepositType type,
            Integer amount,
            Integer balanceAfter,
            Attendance attendance,
            String description
    ) {
        validateCreation(cohortMember, type, amount, balanceAfter);

        this.cohortMember = cohortMember;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.attendance = attendance;
        this.description = description;
    }

    public static DepositHistory initial(CohortMember cohortMember, int amount) {
        return new DepositHistory(
                cohortMember,
                DepositType.INITIAL,
                amount,
                amount,  // 초기 지급이므로 amount = balanceAfter
                null,
                "초기 보증금"
        );
    }

    public static DepositHistory penalty(CohortMember cohortMember,
                                         int penaltyAmount,
                                         Attendance attendance
    ) {
        return new DepositHistory(
                cohortMember,
                DepositType.PENALTY,
                -penaltyAmount,
                cohortMember.getDepositBalance(),  // 차감 후 잔액
                attendance,
                String.format("출결 등록 - ABSENT 패널티 " + penaltyAmount + "원")
        );
    }

    public static DepositHistory refund(CohortMember cohortMember,
                                        int refundAmount,
                                        Attendance attendance
    ) {
        return new DepositHistory(
                cohortMember,
                DepositType.REFUND,
                refundAmount,
                cohortMember.getDepositBalance(),  // 환급 후 잔액
                attendance,
                String.format("출결 수정 - 환급 " + refundAmount + "원")
        );
    }

    private void validateCreation(
            CohortMember cohortMember,
            DepositType type,
            Integer amount,
            Integer balanceAfter
    ) {
        if (cohortMember == null) {
            throw new IllegalArgumentException("기수 회원은 필수입니다");
        }
        if (type == null) {
            throw new IllegalArgumentException("보증금 이력 타입은 필수입니다");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("금액은 양수여야 합니다");
        }
        if (balanceAfter == null || balanceAfter < 0) {
            throw new IllegalArgumentException("잔액은 0 이상이어야 합니다");
        }
    }
}
