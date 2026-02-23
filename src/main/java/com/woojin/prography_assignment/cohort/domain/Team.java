package com.woojin.prography_assignment.cohort.domain;

import com.woojin.prography_assignment.common.BaseTimeEntity;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import javax.swing.plaf.IconUIResource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(name = "team_name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cohort_id", nullable = false, foreignKey = @ForeignKey(name = "fk_team_cohort"))
    private Cohort cohort;

    private Team(String name, Cohort cohort) {
        this.name = name;
        this.cohort = cohort;
    }

    public static Team create(Cohort cohort, String teamName) {
        return new Team(teamName, cohort);
    }

    private void validateCreation(Cohort cohort, String teamName) {
        if (cohort == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT,
                    ErrorCode.INVALID_INPUT.getMessage() + "> 기수는 필수입니다.");
        }

        if (teamName == null || teamName.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT,
                    ErrorCode.INVALID_INPUT.getMessage() + "> 팀 이름은 필수입니다.");
        }
    }
}
