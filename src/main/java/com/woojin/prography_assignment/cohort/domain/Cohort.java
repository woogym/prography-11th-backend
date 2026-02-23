package com.woojin.prography_assignment.cohort.domain;

import com.woojin.prography_assignment.common.BaseTimeEntity;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.BusinessException;
import com.woojin.prography_assignment.common.exception.model.InvalidInputException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cohort extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cohort_id")
    private Long id;

    @Column(name = "cohort_generation", nullable = false, unique = true)
    private Integer generation;

    @Column(name = "cohort_name", length = 500)
    private String name;

    private Cohort(Integer generation, String name) {
        this.generation = generation;
        this.name = name;
    }

    public static Cohort create(Integer generation, String name) {
        return new Cohort(generation, name);
    }

    private void validateCohortNumber(Integer cohortNumber) {
        if (cohortNumber == null || cohortNumber < 0) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "기수는 음수일 수 없습니다.");
        }
    }
}
