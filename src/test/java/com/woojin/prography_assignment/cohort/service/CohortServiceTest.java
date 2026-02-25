package com.woojin.prography_assignment.cohort.service;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.PartType;
import com.woojin.prography_assignment.cohort.domain.Team;
import com.woojin.prography_assignment.cohort.dto.response.CohortDetailResponse;
import com.woojin.prography_assignment.cohort.dto.response.CohortResponse;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import com.woojin.prography_assignment.cohort.repository.PartRepository;
import com.woojin.prography_assignment.cohort.repository.TeamRepository;
import com.woojin.prography_assignment.common.exception.model.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("CohortService 단위 테스트")
class CohortServiceTest {

    @InjectMocks
    private CohortService cohortService;

    @Mock
    private CohortRepository cohortRepository;

    @Mock
    private PartRepository partRepository;

    @Mock
    private TeamRepository teamRepository;

    private static final Long COHORT_ID = 2L;

    private Cohort cohort10;
    private Cohort cohort11;
    private List<Part> parts;
    private List<Team> teams;

    @BeforeEach
    void setUp() {
        cohort10 = Cohort.create(10, "10기");
        cohort11 = Cohort.create(11, "11기");

        parts = List.of(
                Part.create(cohort11, PartType.SERVER),
                Part.create(cohort11, PartType.WEB),
                Part.create(cohort11, PartType.IOS),
                Part.create(cohort11, PartType.ANDROID),
                Part.create(cohort11, PartType.DESIGN)
        );

        teams = List.of(
                Team.create(cohort11, "Team A"),
                Team.create(cohort11, "Team B"),
                Team.create(cohort11, "Team C")
        );
    }

    @Nested
    @DisplayName("전체 기수 조회")
    class GetCohorts {

        @Test
        @DisplayName("성공")
        void success() {
            // Given
            given(cohortRepository.findAllByOrderByGenerationAsc())
                    .willReturn(List.of(cohort10, cohort11));

            // When
            List<CohortResponse> responses = cohortService.getCohorts();

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).generation()).isEqualTo(10);
            assertThat(responses.get(1).generation()).isEqualTo(11);
        }

        @Test
        @DisplayName("빈 목록")
        void empty() {
            // Given
            given(cohortRepository.findAllByOrderByGenerationAsc())
                    .willReturn(List.of());

            // When
            List<CohortResponse> responses = cohortService.getCohorts();

            // Then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("기수 상세 조회")
    class GetCohortDetail {

        @Test
        @DisplayName("성공 (파트 5개, 팀 3개)")
        void success() {
            // Given
            given(cohortRepository.findById(COHORT_ID))
                    .willReturn(Optional.of(cohort11));
            given(partRepository.findByCohortId(COHORT_ID))
                    .willReturn(parts);
            given(teamRepository.findByCohortId(COHORT_ID))
                    .willReturn(teams);

            // When
            CohortDetailResponse response = cohortService.getCohortDetail(COHORT_ID);

            // Then
            assertThat(response.generation()).isEqualTo(11);
            assertThat(response.parts()).hasSize(5);
            assertThat(response.teams()).hasSize(3);
        }

        @Test
        @DisplayName("파트/팀 없음")
        void noParts() {
            // Given
            given(cohortRepository.findById(COHORT_ID))
                    .willReturn(Optional.of(cohort11));
            given(partRepository.findByCohortId(COHORT_ID))
                    .willReturn(List.of());
            given(teamRepository.findByCohortId(COHORT_ID))
                    .willReturn(List.of());

            // When
            CohortDetailResponse response = cohortService.getCohortDetail(COHORT_ID);

            // Then
            assertThat(response.parts()).isEmpty();
            assertThat(response.teams()).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 기수")
        void notFound() {
            // Given
            given(cohortRepository.findById(999L))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> cohortService.getCohortDetail(999L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}