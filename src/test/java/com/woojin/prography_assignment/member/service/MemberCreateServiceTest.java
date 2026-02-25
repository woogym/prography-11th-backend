package com.woojin.prography_assignment.member.service;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.PartType;
import com.woojin.prography_assignment.cohort.domain.Team;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import com.woojin.prography_assignment.cohort.repository.PartRepository;
import com.woojin.prography_assignment.cohort.repository.TeamRepository;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.DuplicateResourceException;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import com.woojin.prography_assignment.deposit.domain.DepositHistory;
import com.woojin.prography_assignment.deposit.repository.DepositRepository;
import com.woojin.prography_assignment.member.domain.CohortMember;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.domain.MemberRole;
import com.woojin.prography_assignment.member.domain.MemberStatus;
import com.woojin.prography_assignment.member.dto.request.CreateMemberRequest;
import com.woojin.prography_assignment.member.dto.response.MemberDetailResponse;
import com.woojin.prography_assignment.member.repository.CohortMemberRepository;
import com.woojin.prography_assignment.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberCreateService 단위 테스트")
class MemberCreateServiceTest {

    @InjectMocks
    private MemberCreateService memberCreateService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CohortRepository cohortRepository;

    @Mock
    private PartRepository partRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private CohortMemberRepository cohortMemberRepository;

    @Mock
    private DepositRepository depositRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String LOGIN_ID = "woojin";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$12$encodedPassword";
    private static final String NAME = "김우진";
    private static final String PHONE = "010-1234-5678";
    private static final Long COHORT_ID = 2L;
    private static final Long PART_ID = 6L;
    private static final Long TEAM_ID = 1L;
    private static final Long MEMBER_ID = 1L;

    private Cohort cohort;
    private Part part;
    private Team team;

    @BeforeEach
    void setUp() {
        cohort = Cohort.create(11, "11기");
        part = Part.create(cohort, PartType.SERVER);
        team = Team.create(cohort, "Team A");
    }

    @Nested
    @DisplayName("회원 생성 성공")
    class Success {

        @Test
        @DisplayName("모든 필드 포함")
        void withAllFields() {
            // Given
            CreateMemberRequest request = new CreateMemberRequest(
                    LOGIN_ID, PASSWORD, NAME, PHONE, COHORT_ID, PART_ID, TEAM_ID
            );

            Member savedMember = createMemberWithId(MEMBER_ID);
            CohortMember savedCohortMember = CohortMember.create(cohort, savedMember, part, team);

            given(memberRepository.existsByLoginId(LOGIN_ID)).willReturn(false);
            given(cohortRepository.findById(COHORT_ID)).willReturn(Optional.of(cohort));
            given(partRepository.findById(PART_ID)).willReturn(Optional.of(part));
            given(teamRepository.findById(TEAM_ID)).willReturn(Optional.of(team));
            given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);

            given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
                Member member = invocation.getArgument(0);
                ReflectionTestUtils.setField(member, "id", MEMBER_ID);
                return member;
            });

            given(cohortMemberRepository.save(any(CohortMember.class))).willReturn(savedCohortMember);
            given(cohortMemberRepository.findByMemberId(MEMBER_ID)).willReturn(Optional.of(savedCohortMember));

            // When
            MemberDetailResponse response = memberCreateService.createMember(request);

            // Then
            assertThat(response.loginId()).isEqualTo(LOGIN_ID);
            assertThat(response.name()).isEqualTo(NAME);
            assertThat(response.phone()).isEqualTo(PHONE);
            assertThat(response.role()).isEqualTo(MemberRole.MEMBER);
            assertThat(response.status()).isEqualTo(MemberStatus.ACTIVE);
        }

        @Test
        @DisplayName("Part와 Team이 null")
        void withNullPartAndTeam() {
            // Given
            CreateMemberRequest request = new CreateMemberRequest(
                    LOGIN_ID, PASSWORD, NAME, PHONE, COHORT_ID, null, null
            );

            Member savedMember = createMemberWithId(MEMBER_ID);
            CohortMember savedCohortMember = CohortMember.create(cohort, savedMember, null, null);

            given(memberRepository.existsByLoginId(LOGIN_ID)).willReturn(false);
            given(cohortRepository.findById(COHORT_ID)).willReturn(Optional.of(cohort));
            given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);

            given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
                Member member = invocation.getArgument(0);
                ReflectionTestUtils.setField(member, "id", MEMBER_ID);
                return member;
            });

            given(cohortMemberRepository.save(any(CohortMember.class))).willReturn(savedCohortMember);
            given(cohortMemberRepository.findByMemberId(MEMBER_ID)).willReturn(Optional.of(savedCohortMember));

            // When
            memberCreateService.createMember(request);

            // Then
            then(partRepository).should(never()).findById(anyLong());
            then(teamRepository).should(never()).findById(anyLong());
        }

        @Test
        @DisplayName("Member 엔티티 올바르게 생성")
        void memberEntityCreated() {
            // Given
            CreateMemberRequest request = new CreateMemberRequest(
                    LOGIN_ID, PASSWORD, NAME, PHONE, COHORT_ID, PART_ID, TEAM_ID
            );

            ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
            Member savedMember = createMemberWithId(MEMBER_ID);
            CohortMember savedCohortMember = CohortMember.create(cohort, savedMember, part, team);

            given(memberRepository.existsByLoginId(LOGIN_ID)).willReturn(false);
            given(cohortRepository.findById(COHORT_ID)).willReturn(Optional.of(cohort));
            given(partRepository.findById(PART_ID)).willReturn(Optional.of(part));
            given(teamRepository.findById(TEAM_ID)).willReturn(Optional.of(team));
            given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);

            given(memberRepository.save(memberCaptor.capture())).willAnswer(invocation -> {
                Member member = invocation.getArgument(0);
                ReflectionTestUtils.setField(member, "id", MEMBER_ID);
                return member;
            });

            given(cohortMemberRepository.save(any(CohortMember.class))).willReturn(savedCohortMember);
            given(cohortMemberRepository.findByMemberId(MEMBER_ID)).willReturn(Optional.of(savedCohortMember));

            // When
            memberCreateService.createMember(request);

            // Then
            Member captured = memberCaptor.getValue();
            assertThat(captured.getLoginId()).isEqualTo(LOGIN_ID);
            assertThat(captured.getName()).isEqualTo(NAME);
            assertThat(captured.getPhone()).isEqualTo(PHONE);
            assertThat(captured.getRole()).isEqualTo(MemberRole.MEMBER);
            assertThat(captured.getStatus()).isEqualTo(MemberStatus.ACTIVE);
            assertThat(captured.isActive()).isTrue();
            assertThat(captured.isWithdraw()).isFalse();
            assertThat(captured.isAdmin()).isFalse();
        }

        @Test
        @DisplayName("초기 보증금 100,000원 지급")
        void initialDepositCreated() {
            // Given
            CreateMemberRequest request = new CreateMemberRequest(
                    LOGIN_ID, PASSWORD, NAME, PHONE, COHORT_ID, PART_ID, TEAM_ID
            );

            Member savedMember = createMemberWithId(MEMBER_ID);
            CohortMember savedCohortMember = CohortMember.create(cohort, savedMember, part, team);

            given(memberRepository.existsByLoginId(LOGIN_ID)).willReturn(false);
            given(cohortRepository.findById(COHORT_ID)).willReturn(Optional.of(cohort));
            given(partRepository.findById(PART_ID)).willReturn(Optional.of(part));
            given(teamRepository.findById(TEAM_ID)).willReturn(Optional.of(team));
            given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);

            given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
                Member member = invocation.getArgument(0);
                ReflectionTestUtils.setField(member, "id", MEMBER_ID);
                return member;
            });

            given(cohortMemberRepository.save(any(CohortMember.class))).willReturn(savedCohortMember);
            given(cohortMemberRepository.findByMemberId(MEMBER_ID)).willReturn(Optional.of(savedCohortMember));

            // When
            memberCreateService.createMember(request);

            // Then
            then(depositRepository).should().save(any(DepositHistory.class));
        }

        @Test
        @DisplayName("비밀번호 인코딩")
        void passwordEncoded() {
            // Given
            CreateMemberRequest request = new CreateMemberRequest(
                    LOGIN_ID, "plainPassword", NAME, PHONE, COHORT_ID, PART_ID, TEAM_ID
            );

            Member savedMember = createMemberWithId(MEMBER_ID);
            CohortMember savedCohortMember = CohortMember.create(cohort, savedMember, part, team);

            given(memberRepository.existsByLoginId(LOGIN_ID)).willReturn(false);
            given(cohortRepository.findById(COHORT_ID)).willReturn(Optional.of(cohort));
            given(partRepository.findById(PART_ID)).willReturn(Optional.of(part));
            given(teamRepository.findById(TEAM_ID)).willReturn(Optional.of(team));
            given(passwordEncoder.encode("plainPassword")).willReturn(ENCODED_PASSWORD);

            given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
                Member member = invocation.getArgument(0);
                ReflectionTestUtils.setField(member, "id", MEMBER_ID);
                return member;
            });

            given(cohortMemberRepository.save(any(CohortMember.class))).willReturn(savedCohortMember);
            given(cohortMemberRepository.findByMemberId(MEMBER_ID)).willReturn(Optional.of(savedCohortMember));

            // When
            memberCreateService.createMember(request);

            // Then
            then(passwordEncoder).should().encode("plainPassword");
        }
    }

    @Nested
    @DisplayName("회원 생성 실패")
    class Failure {

        @Test
        @DisplayName("중복된 loginId")
        void duplicateLoginId() {
            // Given
            CreateMemberRequest request = new CreateMemberRequest(
                    LOGIN_ID, PASSWORD, NAME, PHONE, COHORT_ID, PART_ID, TEAM_ID
            );
            given(memberRepository.existsByLoginId(LOGIN_ID)).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> memberCreateService.createMember(request))
                    .isInstanceOf(DuplicateResourceException.class);
        }

        @Test
        @DisplayName("존재하지 않는 Cohort")
        void cohortNotFound() {
            // Given
            CreateMemberRequest request = new CreateMemberRequest(
                    LOGIN_ID, PASSWORD, NAME, PHONE, 999L, PART_ID, TEAM_ID
            );

            given(memberRepository.existsByLoginId(LOGIN_ID)).willReturn(false);
            given(cohortRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberCreateService.createMember(request))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 Part")
        void partNotFound() {
            // Given
            CreateMemberRequest request = new CreateMemberRequest(
                    LOGIN_ID, PASSWORD, NAME, PHONE, COHORT_ID, 999L, TEAM_ID
            );

            given(memberRepository.existsByLoginId(LOGIN_ID)).willReturn(false);
            given(cohortRepository.findById(COHORT_ID)).willReturn(Optional.of(cohort));
            given(partRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberCreateService.createMember(request))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 Team")
        void teamNotFound() {
            // Given
            CreateMemberRequest request = new CreateMemberRequest(
                    LOGIN_ID, PASSWORD, NAME, PHONE, COHORT_ID, PART_ID, 999L
            );

            given(memberRepository.existsByLoginId(LOGIN_ID)).willReturn(false);
            given(cohortRepository.findById(COHORT_ID)).willReturn(Optional.of(cohort));
            given(partRepository.findById(PART_ID)).willReturn(Optional.of(part));
            given(teamRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberCreateService.createMember(request))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    private Member createMemberWithId(Long id) {
        Member member = Member.createMember(LOGIN_ID, ENCODED_PASSWORD, NAME, PHONE);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }
}