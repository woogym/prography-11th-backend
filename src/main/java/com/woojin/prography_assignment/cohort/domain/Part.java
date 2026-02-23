package com.woojin.prography_assignment.cohort.domain;

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
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cohort_id", nullable = false, foreignKey = @ForeignKey(name = "fk_part_cohort"))
    private Cohort cohort;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_type", nullable = false)
    private PartType partType;

    private Part(Cohort cohort, PartType partType) {
        this.cohort = cohort;
        this.partType = partType;
    }

    public static Part create(Cohort cohort, PartType partType) {
        return new Part(cohort, partType);
    }
}
