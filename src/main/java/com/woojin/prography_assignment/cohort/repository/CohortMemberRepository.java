package com.woojin.prography_assignment.cohort.repository;

import com.woojin.prography_assignment.cohort.domain.CohortMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CohortMemberRepository extends JpaRepository<CohortMember, Long> {

    @Query("""
        select cm
        from CohortMember cm
        join fetch cm.cohort c
        left join fetch cm.part p
        left join fetch cm.team t
        where cm.member.id = :memberId
        """)
    Optional<CohortMember> findByMemberId(@Param("memberId") Long memberId);
}
