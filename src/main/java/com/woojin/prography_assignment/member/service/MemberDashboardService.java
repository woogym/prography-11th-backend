package com.woojin.prography_assignment.member.service;

import com.woojin.prography_assignment.member.domain.CohortMember;
import com.woojin.prography_assignment.cohort.repository.CohortMemberRepository;
import com.woojin.prography_assignment.common.dto.PageResponse;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.domain.MemberStatus;
import com.woojin.prography_assignment.member.dto.response.MemberDashboardResponse;
import com.woojin.prography_assignment.member.repository.MemberRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class MemberDashboardService {

    private final MemberRepository memberRepository;
    private final CohortMemberRepository cohortMemberRepository;

    /**
     * 회원 대시보드 조회
     *
     * 비즈니스 규칙:
     * 1. DB 레벨 필터: status, searchType+searchValue
     * 2. 메모리 레벨 필터: generation, partName, teamName
     * 3. 메모리 필터 적용 시 totalElements/totalPages 재계산
     */
    public PageResponse<MemberDashboardResponse> getDashboard(int page,
                                                              int size,
                                                              String searchType,
                                                              String searchValue,
                                                              Integer generation,
                                                              String partName,
                                                              String teamName,
                                                              MemberStatus status
    ) {
        boolean hasMemoryFilter = hasMemoryFilter(generation, partName, teamName);

        if (hasMemoryFilter) {
            return getDashboardWithMemoryFilter(
                    page, size, searchType, searchValue,
                    generation, partName, teamName, status
            );
        }

        return getDashboardWithDbFilterOnly(
                page, size, searchType, searchValue, status
        );
    }

    private PageResponse<MemberDashboardResponse> getDashboardWithDbFilterOnly(
            int page,
            int size,
            String searchType,
            String searchValue,
            MemberStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> memberPage = findMembers(status, searchType, searchValue, pageable);
        Map<Long, CohortMember> cohortMemberMap = fetchCohortMembersAsMap(memberPage.getContent());
        List<MemberDashboardResponse> content = memberPage.getContent().stream()
                .map(member -> MemberDashboardResponse.from(member, cohortMemberMap.get(member.getId())))
                .toList();

        return PageResponse.of(
                content,
                memberPage.getNumber(),
                memberPage.getSize(),
                memberPage.getTotalElements(),
                memberPage.getTotalPages()
        );
    }

    private PageResponse<MemberDashboardResponse> getDashboardWithMemoryFilter(
            int page,
            int size,
            String searchType,
            String searchValue,
            Integer generation,
            String partName,
            String teamName,
            MemberStatus status
    ) {
        Page<Member> allMembers = findMembers(status, searchType, searchValue, Pageable.unpaged());
        Map<Long, CohortMember> cohortMemberMap = fetchCohortMembersAsMap(allMembers.getContent());
        List<MemberDashboardResponse> allFiltered = allMembers.getContent().stream()
                .map(member -> MemberDashboardResponse.from(member, cohortMemberMap.get(member.getId())))
                .filter(response -> applyMemoryFilter(response, generation, partName, teamName))
                .toList();

        long totalElements = allFiltered.size();
        int totalPages = calculateTotalPages(totalElements, size);

        List<MemberDashboardResponse> pagedContent = applyPagination(allFiltered, page, size);

        return PageResponse.of(
                pagedContent,
                page,
                size,
                totalElements,
                totalPages
        );
    }

    private Page<Member> findMembers(
            MemberStatus status,
            String searchType,
            String searchValue,
            Pageable pageable
    ) {
        return memberRepository.findMembersForDashboard(
                status,
                searchType,
                searchValue,
                pageable
        );
    }

    private Map<Long, CohortMember> fetchCohortMembersAsMap(List<Member> members) {
        List<Long> memberIds = members.stream()
                .map(Member::getId)
                .toList();

        List<CohortMember> cohortMembers = cohortMemberRepository
                .findByMemberIdsWithRelations(memberIds);

        return cohortMembers.stream()
                .collect(Collectors.toMap(
                        cm -> cm.getMember().getId(),
                        cm -> cm
                ));
    }

    private boolean hasMemoryFilter(Integer generation,
                                    String partName,
                                    String teamName) {
        if (generation != null) {
            return true;
        }
        if (partName != null) {
            return true;
        }
        if (teamName != null) {
            return true;
        }
        return false;
    }

    private boolean applyMemoryFilter(MemberDashboardResponse response,
                                      Integer generation,
                                      String partName,
                                      String teamName
    ) {
        if (generation != null) {
            if (!generation.equals(response.generation())) {
                return false;
            }
        }

        if (partName != null) {
            if (!partName.equals(response.partName())) {
                return false;
            }
        }

        if (teamName != null) {
            if (!teamName.equals(response.teamName())) {
                return false;
            }
        }

        return true;
    }

    private List<MemberDashboardResponse> applyPagination(List<MemberDashboardResponse> allData,
                                                          int page,
                                                          int size) {
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allData.size());

        if (startIndex >= allData.size()) {
            return List.of();
        }

        return allData.subList(startIndex, endIndex);
    }

    private int calculateTotalPages(long totalElements, int size) {
        return (int) Math.ceil((double) totalElements / size);
    }
}
