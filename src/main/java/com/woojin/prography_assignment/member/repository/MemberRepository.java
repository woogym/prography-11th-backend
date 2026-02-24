package com.woojin.prography_assignment.member.repository;

import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.domain.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginId(String loginId);

    @Query("""
        SELECT m
        FROM Member m
        WHERE (:status IS NULL OR m.status = :status)
        AND (:searchType IS NULL OR :searchValue IS NULL
            OR (:searchType = 'name' AND m.name LIKE %:searchValue%)
            OR (:searchType = 'loginId' AND m.loginId LIKE %:searchValue%)
            OR (:searchType = 'phone' AND m.phone LIKE %:searchValue%))
        ORDER BY m.createdAt DESC
        """)
    Page<Member> findMembersForDashboard(
            @Param("status") MemberStatus status,
            @Param("searchType") String searchType,
            @Param("searchValue") String searchValue,
            Pageable pageable
    );
}
