package com.woojin.prography_assignment.member.repository;

import com.woojin.prography_assignment.member.domain.CohortMember;
import java.util.List;
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
        order by cm.cohort.generation DESC
        limit 1
        """)
    Optional<CohortMember> findByMemberId(@Param("memberId") Long memberId);

    // 여러 회원의 CohortMember 일괄 조회
    @Query("""
        SELECT cm
        FROM CohortMember cm
        JOIN FETCH cm.cohort c
        LEFT JOIN FETCH cm.part p
        LEFT JOIN FETCH cm.team t
        WHERE cm.member.id IN :memberIds
        """)
    List<CohortMember> findByMemberIdsWithRelations(@Param("memberIds") List<Long> memberIds);

    // 특정 멤버의 특정 기수를 통한 조회
    @Query("""
        SELECT cm FROM CohortMember cm
        WHERE cm.member.id = :memberId
        AND cm.cohort.id = :cohortId
        """)
    Optional<CohortMember> findByMemberIdAndCohortId(@Param("memberId") Long memberId, @Param("cohortId") Long cohortId);
}
