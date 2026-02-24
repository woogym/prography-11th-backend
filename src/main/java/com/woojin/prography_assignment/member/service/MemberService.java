package com.woojin.prography_assignment.member.service;

import com.woojin.prography_assignment.member.domain.CohortMember;
import com.woojin.prography_assignment.cohort.repository.CohortMemberRepository;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.BusinessException;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.dto.response.MemberDetailResponse;
import com.woojin.prography_assignment.member.dto.response.MemberResponse;
import com.woojin.prography_assignment.member.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberService {

    private final MemberRepository memberRepository;
    private final CohortMemberRepository cohortMemberRepository;

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND,
                        ErrorCode.MEMBER_NOT_FOUND.getMessage()));

        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberDetailResponse getMemberDetail(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND,
                        ErrorCode.MEMBER_NOT_FOUND.getMessage()));

        CohortMember cohortMember = cohortMemberRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COHORT_MEMBER_NOT_FOUND,
                        ErrorCode.COHORT_MEMBER_NOT_FOUND.getMessage()));

        return MemberDetailResponse.from(member, cohortMember);
    }
}
