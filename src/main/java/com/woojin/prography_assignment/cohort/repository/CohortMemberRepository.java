package com.woojin.prography_assignment.cohort.repository;

import com.woojin.prography_assignment.cohort.domain.CohortMember;
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
        """)
    Optional<CohortMember> findByMemberId(@Param("memberId") Long memberId);

    // 단일 회원의 CohortMember 조회
    @Query("""
        SELECT cm
        FROM CohortMember cm
        JOIN FETCH cm.cohort c
        LEFT JOIN FETCH cm.part p
        LEFT JOIN FETCH cm.team t
        WHERE cm.member.id = :memberId
        """)
    Optional<CohortMember> findByMemberIdWithRelations(@Param("memberId") Long memberId);

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
}
