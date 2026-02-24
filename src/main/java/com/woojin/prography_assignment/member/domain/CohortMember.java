package com.woojin.prography_assignment.member.domain;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.Team;
import com.woojin.prography_assignment.common.BaseTimeEntity;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.ExcuseLimitExceededException;
import com.woojin.prography_assignment.common.exception.model.InsufficientDepositException;
import com.woojin.prography_assignment.common.exception.model.InvalidInputException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class CohortMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cohort_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cohort_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cohort_member_cohort"))
    private Cohort cohort;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cohort_member_member"))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", foreignKey = @ForeignKey(name = "fk_cohort_member_part"))
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "fk_cohort_member_team"))
    private Team team;

    @Column(name = "deposit_balance", nullable = false)
    private Integer depositBalance;

    @Column(name = "excuse_count", nullable = false)
    private Integer excuseCount;

    private static final int INITIAL_DEPOSIT = 100_000;
    private static final int MAX_EXCUSE_COUNT = 3;

    private CohortMember(Cohort cohort, Member member, Part part, Team team) {
        validationCreation(cohort, member);

        this.cohort = cohort;
        this.member = member;
        this.part = part;
        this.team = team;
        this.depositBalance = INITIAL_DEPOSIT;
        this.excuseCount = 0;
    }

    public static CohortMember create(Cohort cohort, Member member, Part part, Team team) {
        return new CohortMember(cohort, member, part, team);
    }

    public static CohortMember createForAdmin(Cohort cohort, Member member) {
        return new CohortMember(cohort, member, null, null);
    }

    public void updatePart(Part part) {
        this.part = part;
    }

    public void updateTeam(Team team) {
        this.team = team;
    }

    public void deductDeposit(int amount) {
        if (amount < 0) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "차감 금액은 양수여야 합니다");
        }
        if (this.depositBalance < amount) {
            throw new InsufficientDepositException(
                    String.format("보증금 잔액이 부족합니다. 잔액: %d원, 차감 금액: %d원", this.depositBalance, amount)
            );
        }
        this.depositBalance -= amount;
    }

    public boolean canDeduct(int amount) {
        return this.depositBalance >= amount;
    }

    public void refundDeposit(int amount) {
        if (amount < 0) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "환급 금액은 양수여야 합니다");
        }
        this.depositBalance += amount;
    }

    public void incrementExcuseCount() {
        if (this.excuseCount >= MAX_EXCUSE_COUNT) {
            throw new ExcuseLimitExceededException(
                    String.format("공결 횟수는 최대 %d회까지 허용됩니다", MAX_EXCUSE_COUNT)
            );
        }
        this.excuseCount++;
    }

    public void decrementExcuseCount() {
        if (this.excuseCount <= 0) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "공결 횟수는 0 미만이 될 수 없습니다");
        }
        this.excuseCount--;
    }

    public boolean canUseExcuse() {
        return this.excuseCount < MAX_EXCUSE_COUNT;
    }

    private void validationCreation(Cohort cohort, Member member) {
        validationGenerationIsNull(cohort);

        if (member == null) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "회원은 필수입니다.");
        }
    }

    private void validationGenerationIsNull(Cohort cohort) {
        if (cohort == null) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "기수는 필수입니다.");
        }
    }
}
