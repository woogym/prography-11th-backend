package com.woojin.prography_assignment.member.service;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.PartType;
import com.woojin.prography_assignment.cohort.domain.Team;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import com.woojin.prography_assignment.cohort.repository.PartRepository;
import com.woojin.prography_assignment.cohort.repository.TeamRepository;
import com.woojin.prography_assignment.common.exception.model.BusinessException;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.deposit.domain.DepositHistory;
import com.woojin.prography_assignment.deposit.repository.DepositRepository;
import com.woojin.prography_assignment.member.domain.CohortMember;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.dto.request.MemberUpdateRequest;
import com.woojin.prography_assignment.member.dto.response.MemberDeleteResponse;
import com.woojin.prography_assignment.member.dto.response.MemberDetailResponse;
import com.woojin.prography_assignment.member.dto.response.MemberResponse;
import com.woojin.prography_assignment.member.repository.CohortMemberRepository;
import com.woojin.prography_assignment.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 단위 테스트")
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CohortMemberRepository cohortMemberRepository;

    @Mock
    private PartRepository partRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private CohortRepository cohortRepository;

    @Mock
    private DepositRepository depositHistoryRepository;

    private static final String NAME = "김우진";
    private static final String PHONE = "010-1234-5678";
    private static final Long MEMBER_ID = 1L;
    private static final Long COHORT_ID = 2L;
    private static final Long PART_ID = 6L;
    private static final Long TEAM_ID = 1L;

    private Member member;
    private Cohort cohort;
    private Part part;
    private Team team;
    private CohortMember cohortMember;

    @BeforeEach
    void setUp() {
        member = createMemberWithId(MEMBER_ID);
        cohort = Cohort.create(11, "11기");
        part = Part.create(cohort, PartType.SERVER);
        team = Team.create(cohort, "Team A");
        cohortMember = CohortMember.create(cohort, member, part, team);
    }

    @Nested
    @DisplayName("회원 조회")
    class GetMember {

        @Test
        @DisplayName("성공")
        void success() {
            // Given
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));

            // When
            MemberResponse response = memberService.getMember(MEMBER_ID);

            // Then
            assertThat(response.name()).isEqualTo(NAME);
        }

        @Test
        @DisplayName("존재하지 않는 회원")
        void notFound() {
            // Given
            given(memberRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberService.getMember(999L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("회원 상세 조회")
    class GetMemberDetail {

        @Test
        @DisplayName("성공")
        void success() {
            // Given
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
            given(cohortMemberRepository.findByMemberId(MEMBER_ID))
                    .willReturn(Optional.of(cohortMember));

            // When
            MemberDetailResponse response = memberService.getMemberDetail(MEMBER_ID);

            // Then
            assertThat(response.name()).isEqualTo(NAME);
            assertThat(response.generation()).isEqualTo(11);
        }
    }

    @Nested
    @DisplayName("회원 수정")
    class UpdateMember {

        @Test
        @DisplayName("기본 정보만 수정")
        void basicInfo() {
            // Given
            MemberUpdateRequest request = new MemberUpdateRequest(
                    "김우진2", "010-9999-9999", null, null, null
            );

            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
            given(cohortMemberRepository.findByMemberId(MEMBER_ID))
                    .willReturn(Optional.of(cohortMember));

            // When
            MemberDetailResponse response = memberService.updateMember(MEMBER_ID, request);

            // Then
            assertThat(response.name()).isEqualTo("김우진2");
            assertThat(response.phone()).isEqualTo("010-9999-9999");
        }

        @Test
        @DisplayName("새로운 기수 등록 (보증금 지급)")
        void newCohort() {
            // Given
            Cohort cohort10 = Cohort.create(10, "10기");
            ReflectionTestUtils.setField(cohort10, "id", 10L);  // ← ID 설정 필요!

            MemberUpdateRequest request = new MemberUpdateRequest(
                    NAME, PHONE, 10L, PART_ID, TEAM_ID
            );

            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
            given(cohortRepository.findById(10L)).willReturn(Optional.of(cohort10));
            given(cohortMemberRepository.findByMemberIdAndCohortId(MEMBER_ID, 10L))
                    .willReturn(Optional.empty());
            given(partRepository.findById(PART_ID)).willReturn(Optional.of(part));
            given(teamRepository.findById(TEAM_ID)).willReturn(Optional.of(team));
            given(cohortMemberRepository.findByMemberId(MEMBER_ID))
                    .willReturn(Optional.of(cohortMember));

            // When
            memberService.updateMember(MEMBER_ID, request);

            // Then
            then(cohortMemberRepository).should().save(any(CohortMember.class));
            then(depositHistoryRepository).should().save(any(DepositHistory.class));
        }

        @Test
        @DisplayName("기존 기수 파트/팀 변경")
        void updatePartTeam() {
            // Given
            MemberUpdateRequest request = new MemberUpdateRequest(
                    NAME, PHONE, null, PART_ID, TEAM_ID
            );

            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
            given(cohortMemberRepository.findByMemberId(MEMBER_ID))
                    .willReturn(Optional.of(cohortMember));
            given(partRepository.findById(PART_ID)).willReturn(Optional.of(part));
            given(teamRepository.findById(TEAM_ID)).willReturn(Optional.of(team));

            // When
            memberService.updateMember(MEMBER_ID, request);

            // Then
            then(partRepository).should().findById(PART_ID);
            then(teamRepository).should().findById(TEAM_ID);
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    class WithdrawMember {

        @Test
        @DisplayName("성공")
        void success() {
            // Given
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));

            // When
            MemberDeleteResponse response = memberService.withdrawnMember(MEMBER_ID);

            // Then
            assertThat(member.isWithdraw()).isTrue();
        }

        @Test
        @DisplayName("이미 탈퇴한 회원")
        void alreadyWithdrawn() {
            // Given
            member.withdraw();
            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));

            // When & Then
            assertThatThrownBy(() -> memberService.withdrawnMember(MEMBER_ID))
                    .isInstanceOf(BusinessException.class);
        }
    }

    private Member createMemberWithId(Long id) {
        Member member = Member.createMember("woojin", "encoded", NAME, PHONE);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }
}