package com.woojin.prography_assignment.member.service;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.PartType;
import com.woojin.prography_assignment.cohort.domain.Team;
import com.woojin.prography_assignment.common.dto.PageResponse;
import com.woojin.prography_assignment.member.domain.CohortMember;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.domain.MemberStatus;
import com.woojin.prography_assignment.member.dto.response.MemberDashboardResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberDashboardService 단위 테스트")
class MemberDashboardServiceTest {

    @InjectMocks
    private MemberDashboardService memberDashboardService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CohortMemberRepository cohortMemberRepository;

    private static final String NAME = "김우진";
    private static final String PHONE = "010-1234-5678";
    private static final Long MEMBER_ID = 1L;

    private Cohort cohort;
    private Part serverPart;
    private Part webPart;
    private Team teamA;
    private Member member;
    private CohortMember cohortMember;

    @BeforeEach
    void setUp() {
        cohort = Cohort.create(11, "11기");
        serverPart = Part.create(cohort, PartType.SERVER);
        webPart = Part.create(cohort, PartType.WEB);
        teamA = Team.create(cohort, "Team A");

        member = createMemberWithId(MEMBER_ID, "woojin", NAME);
        cohortMember = createCohortMember(member, serverPart, teamA);
    }

    @Nested
    @DisplayName("DB 필터만 사용")
    class DbFilterOnly {

        @Test
        @DisplayName("필터 없이 전체 조회")
        void noFilter() {
            // Given
            Page<Member> memberPage = new PageImpl<>(List.of(member), PageRequest.of(0, 10), 1);

            given(memberRepository.findMembersForDashboard(null, null, null, PageRequest.of(0, 10)))
                    .willReturn(memberPage);
            given(cohortMemberRepository.findByMemberIdsWithRelations(List.of(MEMBER_ID)))
                    .willReturn(List.of(cohortMember));

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    0, 10, null, null, null, null, null, null
            );

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getTotalElements()).isEqualTo(1);
            assertThat(response.getTotalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("status 필터")
        void statusFilter() {
            // Given
            Page<Member> memberPage = new PageImpl<>(List.of(member), PageRequest.of(0, 10), 1);

            given(memberRepository.findMembersForDashboard(
                    eq(MemberStatus.ACTIVE), isNull(), isNull(), any(Pageable.class))
            ).willReturn(memberPage);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(List.of(cohortMember));

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    0, 10, null, null, null, null, null, MemberStatus.ACTIVE
            );

            // Then
            assertThat(response.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("검색 필터")
        void searchFilter() {
            // Given
            Page<Member> memberPage = new PageImpl<>(List.of(member), PageRequest.of(0, 10), 1);

            given(memberRepository.findMembersForDashboard(
                    isNull(), eq("name"), eq(NAME), any(Pageable.class))
            ).willReturn(memberPage);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(List.of(cohortMember));

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    0, 10, "name", NAME, null, null, null, null
            );

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).name()).isEqualTo(NAME);
        }
    }

    @Nested
    @DisplayName("메모리 필터 사용")
    class MemoryFilter {

        @Test
        @DisplayName("generation 필터")
        void generationFilter() {
            // Given
            Member member2 = createMemberWithId(2L, "user2", NAME);
            Cohort cohort10 = Cohort.create(10, "10기");
            CohortMember cohortMember10 = createCohortMember(member2, serverPart, teamA);
            ReflectionTestUtils.setField(cohortMember10, "cohort", cohort10);

            Page<Member> allMembers = new PageImpl<>(List.of(member, member2));

            given(memberRepository.findMembersForDashboard(
                    isNull(), isNull(), isNull(), eq(Pageable.unpaged()))
            ).willReturn(allMembers);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(List.of(cohortMember, cohortMember10));

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    0, 10, null, null, 11, null, null, null
            );

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).generation()).isEqualTo(11);
        }

        @Test
        @DisplayName("partName 필터")
        void partNameFilter() {
            // Given
            Member member2 = createMemberWithId(2L, "user2", NAME);
            CohortMember cohortMember2 = createCohortMember(member2, webPart, teamA);

            Page<Member> allMembers = new PageImpl<>(List.of(member, member2));

            given(memberRepository.findMembersForDashboard(
                    isNull(), isNull(), isNull(), eq(Pageable.unpaged()))
            ).willReturn(allMembers);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(List.of(cohortMember, cohortMember2));

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    0, 10, null, null, null, "SERVER", null, null
            );

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).partName()).isEqualTo("SERVER");
        }

        @Test
        @DisplayName("teamName 필터")
        void teamNameFilter() {
            // Given
            Team teamB = Team.create(cohort, "Team B");
            Member member2 = createMemberWithId(2L, "user2", NAME);
            CohortMember cohortMember2 = createCohortMember(member2, serverPart, teamB);

            Page<Member> allMembers = new PageImpl<>(List.of(member, member2));

            given(memberRepository.findMembersForDashboard(
                    isNull(), isNull(), isNull(), eq(Pageable.unpaged()))
            ).willReturn(allMembers);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(List.of(cohortMember, cohortMember2));

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    0, 10, null, null, null, null, "Team A", null
            );

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).teamName()).isEqualTo("Team A");
        }

        @Test
        @DisplayName("복합 필터 (generation + partName)")
        void combinedFilter() {
            // Given
            Member member2 = createMemberWithId(2L, "user2", NAME);
            CohortMember cohortMember2 = createCohortMember(member2, webPart, teamA);

            Page<Member> allMembers = new PageImpl<>(List.of(member, member2));

            given(memberRepository.findMembersForDashboard(
                    isNull(), isNull(), isNull(), eq(Pageable.unpaged()))
            ).willReturn(allMembers);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(List.of(cohortMember, cohortMember2));

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    0, 10, null, null, 11, "SERVER", null, null
            );

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).generation()).isEqualTo(11);
            assertThat(response.getContent().get(0).partName()).isEqualTo("SERVER");
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryTests {

        @Test
        @DisplayName("첫 페이지, 크기 1")
        void firstPageSizeOne() {
            // Given
            Page<Member> memberPage = new PageImpl<>(List.of(member), PageRequest.of(0, 1), 5);

            given(memberRepository.findMembersForDashboard(
                    isNull(), isNull(), isNull(), any(Pageable.class))
            ).willReturn(memberPage);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(List.of(cohortMember));

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    0, 1, null, null, null, null, null, null
            );

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getPage()).isEqualTo(0);
            assertThat(response.getSize()).isEqualTo(1);
            assertThat(response.getTotalPages()).isEqualTo(5);
        }

        @Test
        @DisplayName("마지막 페이지")
        void lastPage() {
            // Given
            Page<Member> memberPage = new PageImpl<>(List.of(member), PageRequest.of(2, 1), 3);

            given(memberRepository.findMembersForDashboard(
                    isNull(), isNull(), isNull(), any(Pageable.class))
            ).willReturn(memberPage);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(List.of(cohortMember));

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    2, 1, null, null, null, null, null, null
            );

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getPage()).isEqualTo(2);
        }

        @Test
        @DisplayName("범위 밖 페이지 (메모리 필터)")
        void outOfRangePageMemoryFilter() {
            // Given
            Page<Member> allMembers = new PageImpl<>(List.of(member));

            given(memberRepository.findMembersForDashboard(
                    isNull(), isNull(), isNull(), eq(Pageable.unpaged()))
            ).willReturn(allMembers);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(List.of(cohortMember));

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    10, 10, null, null, 11, null, null, null
            );

            // Then
            assertThat(response.getContent()).isEmpty();
            assertThat(response.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("빈 결과")
        void emptyResult() {
            // Given
            Page<Member> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

            given(memberRepository.findMembersForDashboard(
                    isNull(), isNull(), isNull(), any(Pageable.class))
            ).willReturn(emptyPage);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(List.of());

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    0, 10, null, null, null, null, null, null
            );

            // Then
            assertThat(response.getContent()).isEmpty();
            assertThat(response.getTotalElements()).isEqualTo(0);
            assertThat(response.getTotalPages()).isEqualTo(0);
        }

        @Test
        @DisplayName("메모리 필터로 모두 필터링됨")
        void allFilteredOut() {
            // Given
            Page<Member> allMembers = new PageImpl<>(List.of(member));

            given(memberRepository.findMembersForDashboard(
                    isNull(), isNull(), isNull(), eq(Pageable.unpaged()))
            ).willReturn(allMembers);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(List.of(cohortMember));

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    0, 10, null, null, 10, null, null, null
            );

            // Then
            assertThat(response.getContent()).isEmpty();
            assertThat(response.getTotalElements()).isEqualTo(0);
            assertThat(response.getTotalPages()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("페이징 재계산")
    class PaginationRecalculation {

        @Test
        @DisplayName("메모리 필터 후 페이지 크기 조정")
        void recalculatePages() {
            // Given
            List<Member> members = List.of(
                    createMemberWithId(1L, "user1", NAME),
                    createMemberWithId(2L, "user2", NAME),
                    createMemberWithId(3L, "user3", NAME),
                    createMemberWithId(4L, "user4", NAME),
                    createMemberWithId(5L, "user5", NAME)
            );

            List<CohortMember> cohortMembers = members.stream()
                    .map(m -> createCohortMember(m, serverPart, teamA))
                    .toList();

            Page<Member> allMembers = new PageImpl<>(members);

            given(memberRepository.findMembersForDashboard(
                    isNull(), isNull(), isNull(), eq(Pageable.unpaged()))
            ).willReturn(allMembers);
            given(cohortMemberRepository.findByMemberIdsWithRelations(anyList()))
                    .willReturn(cohortMembers);

            // When
            PageResponse<MemberDashboardResponse> response = memberDashboardService.getDashboard(
                    0, 2, null, null, 11, null, null, null
            );

            // Then
            assertThat(response.getContent()).hasSize(2);
            assertThat(response.getTotalElements()).isEqualTo(5);
            assertThat(response.getTotalPages()).isEqualTo(3);
        }
    }

    private Member createMemberWithId(Long id, String loginId, String name) {
        Member member = Member.createMember(loginId, "encoded", name, PHONE);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private CohortMember createCohortMember(Member member, Part part, Team team) {
        CohortMember cm = CohortMember.create(cohort, member, part, team);
        ReflectionTestUtils.setField(cm, "cohort", cohort);
        return cm;
    }
}