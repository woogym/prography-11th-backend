package com.woojin.prography_assignment.member.repository;

import com.woojin.prography_assignment.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginId(String loginId);

}
