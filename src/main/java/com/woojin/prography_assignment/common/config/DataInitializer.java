package com.woojin.prography_assignment.common.config;

import com.woojin.prography_assignment.cohort.domain.Cohort;
import com.woojin.prography_assignment.member.domain.CohortMember;
import com.woojin.prography_assignment.cohort.domain.Part;
import com.woojin.prography_assignment.cohort.domain.PartType;
import com.woojin.prography_assignment.cohort.domain.Team;
import com.woojin.prography_assignment.cohort.repository.CohortMemberRepository;
import com.woojin.prography_assignment.cohort.repository.CohortRepository;
import com.woojin.prography_assignment.cohort.repository.PartRepository;
import com.woojin.prography_assignment.cohort.repository.TeamRepository;
import com.woojin.prography_assignment.deposit.domain.DepositHistory;
import com.woojin.prography_assignment.deposit.repository.DepositRepository;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final int INITIAL_DEPOSIT = 100_000;

    private final CohortRepository cohortRepository;
    private final PartRepository partRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final DepositRepository depositHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("시드 데이터 초기화 시작");

        // 멱등성 보장: 이미 데이터가 있으면 스킵
        if (memberRepository.existsByLoginId("admin")) {
            log.info("시드 데이터가 이미 존재하여 초기화를 스킵");
            return;
        }

        List<Cohort> cohorts = createCohorts();
        log.info("기수 2개 생성");

        createParts(cohorts);
        log.info("파트 10개 생성");

        List<Team> teams = createTeams(cohorts.get(1));  // 11기
        log.info("팀 3개 생성 (11기)");

        Member admin = createAdmin();
        log.info("관리자 계정 생성 (loginId: admin)");

        CohortMember adminCohortMember = createAdminCohortMember(
                cohorts.get(1),
                admin
        );
        log.info("관리자 11기 배정");

        createAdminDepositHistory(adminCohortMember);
        log.info("관리자 초기 보증금 100,000원 설정 ");

        log.info("시드 데이터 초기화 완료");
    }

    private List<Cohort> createCohorts() {
        List<Cohort> cohorts = new ArrayList<>();

        Cohort cohort10 = Cohort.create(10, "10기");
        Cohort cohort11 = Cohort.create(11, "11기");

        cohorts.add(cohortRepository.save(cohort10));
        cohorts.add(cohortRepository.save(cohort11));

        return cohorts;
    }

    private void createParts(List<Cohort> cohorts) {
        PartType[] partTypes = PartType.values();

        for (Cohort cohort : cohorts) {
            for (PartType partType : partTypes) {
                Part part = Part.create(cohort, partType);
                partRepository.save(part);
            }
        }
    }

    private List<Team> createTeams(Cohort cohort11) {
        List<Team> teams = new ArrayList<>();

        Team teamA = Team.create(cohort11, "Team A");
        Team teamB = Team.create(cohort11, "Team B");
        Team teamC = Team.create(cohort11, "Team C");

        teams.add(teamRepository.save(teamA));
        teams.add(teamRepository.save(teamB));
        teams.add(teamRepository.save(teamC));

        return teams;
    }

    private Member createAdmin() {
        String encodedPassword = passwordEncoder.encode("admin1234");

        Member admin = Member.createAdmin(
                "admin",
                encodedPassword,
                "관리자",
                "관리자"
        );

        return memberRepository.save(admin);
    }

    private CohortMember createAdminCohortMember(
            Cohort cohort,
            Member admin
    ) {
        CohortMember cohortMember = CohortMember.createForAdmin(cohort, admin);
        return cohortMemberRepository.save(cohortMember);
    }

    private void createAdminDepositHistory(CohortMember adminCohortMember) {
        DepositHistory depositHistory = DepositHistory.initial(
                adminCohortMember,
                INITIAL_DEPOSIT
        );
        depositHistoryRepository.save(depositHistory);
    }
}
