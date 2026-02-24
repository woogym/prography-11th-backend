package com.woojin.prography_assignment.member.service;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.Team;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import com.woojin.prography_assignment.cohort.repository.PartRepository;
import com.woojin.prography_assignment.cohort.repository.TeamRepository;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.deposit.domain.DepositHistory;
import com.woojin.prography_assignment.deposit.repository.DepositRepository;
import com.woojin.prography_assignment.member.domain.CohortMember;
import com.woojin.prography_assignment.member.repository.CohortMemberRepository;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.dto.request.MemberUpdateRequest;
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

    private static final int INITIAL_DEPOSIT = 100_000;

    private final MemberRepository memberRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final PartRepository partRepository;
    private final TeamRepository teamRepository;
    private final CohortRepository cohortRepository;
    private final DepositRepository depositHistoryRepository;

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long id) {
        Member member = findMemberById(id);

        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberDetailResponse getMemberDetail(Long id) {
        Member member = findMemberById(id);
        CohortMember cohortMember = findCohortMemberByMemberId(member.getId());

        return MemberDetailResponse.from(member, cohortMember);
    }

    @Transactional
    public MemberDetailResponse updateMember(Long id, MemberUpdateRequest request) {

        Member member = findMemberById(id);
        member.updateInfo(request.name(), request.phone());
        updateCohortAffiliation(member, request);

        CohortMember cohortMember = findCohortMemberByMemberId(member.getId());

        return MemberDetailResponse.from(member, cohortMember);
    }

    private void updateCohortAffiliation(Member member, MemberUpdateRequest request) {
        if (request.cohortId() != null) {
            updateOrCreateCohortMember(member, request);
        } else {
            updateCurrentCohortMember(member.getId(), request);
        }
    }

    private void updateOrCreateCohortMember(Member member, MemberUpdateRequest request) {
        Cohort cohort = findCohortById(request.cohortId());

        CohortMember cohortMember = cohortMemberRepository
                .findByMemberIdAndCohortId(member.getId(), cohort.getId())
                .orElse(null);

        if (cohortMember == null) {
            createNewCohortMember(member, cohort, request);
        } else {
            updateCohortMemberPartAndTeam(cohortMember, request);
        }
    }

    private void createNewCohortMember(Member member,
                                               Cohort cohort,
                                               MemberUpdateRequest request) {

        Part part = findPartOrNull(request.partId());
        Team team = findTeamOrNull(request.teamId());

        CohortMember cohortMember = CohortMember.create(cohort, member, part, team);
        cohortMemberRepository.save(cohortMember);

        DepositHistory depositHistory = DepositHistory.initial(cohortMember, INITIAL_DEPOSIT);
        depositHistoryRepository.save(depositHistory);
    }

    private void updateCurrentCohortMember(Long memberId, MemberUpdateRequest request) {
        CohortMember cohortMember = findCohortMemberByMemberId(memberId);
        updateCohortMemberPartAndTeam(cohortMember, request);
    }

    private void updateCohortMemberPartAndTeam(
            CohortMember cohortMember,
            MemberUpdateRequest request
    ) {
        if (request.partId() != null) {
            Part part = findPartById(request.partId());
            cohortMember.updatePart(part);
        }

        if (request.teamId() != null) {
            Team team = findTeamById(request.teamId());
            cohortMember.updateTeam(team);
        }
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private CohortMember findCohortMemberByMemberId(Long memberId) {
        return cohortMemberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COHORT_MEMBER_NOT_FOUND));
    }

    private Cohort findCohortById(Long cohortId) {
        return cohortRepository.findById(cohortId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COHORT_NOT_FOUND));
    }

    private Part findPartById(Long partId) {
        return partRepository.findById(partId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PART_NOT_FOUND));
    }

    private Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_FOUND));
    }

    private Part findPartOrNull(Long partId) {
        if (partId == null) {
            return null;
        }
        return findPartById(partId);
    }

    private Team findTeamOrNull(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return findTeamById(teamId);
    }
}
