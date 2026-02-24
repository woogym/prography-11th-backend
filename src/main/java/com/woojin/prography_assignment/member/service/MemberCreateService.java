package com.woojin.prography_assignment.member.service;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.domain.CohortMember;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.Team;
import com.woojin.prography_assignment.cohort.repository.CohortMemberRepository;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import com.woojin.prography_assignment.cohort.repository.PartRepository;
import com.woojin.prography_assignment.cohort.repository.TeamRepository;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.DuplicateResourceException;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.deposit.domain.DepositHistory;
import com.woojin.prography_assignment.deposit.repository.DepositRepository;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.dto.request.CreateMemberRequest;
import com.woojin.prography_assignment.member.dto.response.MemberDetailResponse;
import com.woojin.prography_assignment.member.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCreateService {

    private static final int INITIAL_DEPOSIT = 100_000;

    private final MemberRepository memberRepository;
    private final CohortRepository cohortRepository;
    private final PartRepository partRepository;
    private final TeamRepository teamRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final DepositRepository depositRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberDetailResponse createMember(CreateMemberRequest request) {
        validateLoginIdUnique(request.loginId());

        Cohort cohort = findCohortById(request.cohortId());
        Part part = findPartByIdIfPresent(request.partId());
        Team team = findTeamByIdIfPresent(request.teamId());

        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = Member.createMember(
                request.loginId(),
                encodedPassword,
                request.name(),
                request.phone()
        );
        memberRepository.save(member);

        CohortMember cohortMember = CohortMember.create(cohort, member, part, team);
        cohortMemberRepository.save(cohortMember);

        DepositHistory depositHistory = DepositHistory.initial(cohortMember, INITIAL_DEPOSIT);
        depositRepository.save(depositHistory);

        CohortMember cohortMemberWithRelations = cohortMemberRepository
                .findByMemberId(member.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COHORT_MEMBER_NOT_FOUND,
                        ErrorCode.COHORT_MEMBER_NOT_FOUND.getMessage()));

        return MemberDetailResponse.from(member, cohortMemberWithRelations);
    }

    private void validateLoginIdUnique(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_LOGIN_ID,
                    ErrorCode.DUPLICATE_LOGIN_ID.getMessage());
        }
    }

    private Cohort findCohortById(Long cohortId) {
        return cohortRepository.findById(cohortId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COHORT_NOT_FOUND));
    }

    private Part findPartByIdIfPresent(Long partId) {
        if (partId == null) {
            return null;
        }

        return partRepository.findById(partId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PART_NOT_FOUND));
    }

    private Team findTeamByIdIfPresent(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_FOUND));
    }
}
