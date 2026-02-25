## 1. N + 1 문제

### Problem
회원 생성 후 응답을 만들때 'CohortMember'의 연관 엔티티들을 조회하면서 N+1 발생 가능성이 있었습니다.
```java
CohortMember cohortMember = cohortMemberRepository.findById(id);
cohortMember.getCohort.getCohortNumber();
cohortMember.getPart.getPartType();
cohortMember.getTeam.getTeamName();
```
이렇게 3개의 연관 엔티티를 조회혀먼서 총 4번 쿼리가 발생했습니다.

이를 fetch join을 사용해서 한 번의 쿼리로 모든 데이터를 조회했습니다.
```java
@Query("""
    SELECT cm FROM CohortMember cm
    JOIN FETCH cm.cohort
    LEFT JOIN FETCH cm.part
    LEFT JOIN FETCH cm.team
    WHERE cm.member.id = :memberId
""")
Optional findByMemberIdWithRelations(@Param("memberId") Long memberId);
```

**left join fetch**
- 'team', 'part'는 요구사항중 필수 항목이 아니기에 nullable입니다.
- inner join시에 null값은 조회되지 않기에 left join fetch를 사용하여 팀과 파트가 없더라도 조회가 가능하도록 했습니다.

---

## 2. 엔티티 생성 원자성 보장

### 고려한 상황
회원 생성시에 3개의 엔티티를 생성해야합니다. \
``` Member, CohortMember, DepositHistory ```
중간에 실패하면 데이터 정합성을 보장할 수 없습니다.

### 해결 방법
3개의 엔티티 생성을 하나의 트랜잭션으로 묶었습니다.

### 결과
3개 모두 저장되거나, 모두 저장되지 않게하였고,
부분적으로 저장되어 데이터 정합성이 깨지는 상황을 방지했습니다.

---

### 검증 순서 최적화
```java
validateLoginIdUniqueness(request.loginId()); // 가벼운 쿼리

// 참조 무결성: 외래키 검증 - 무거운 쿼리
Cohort cohort = findCohortById(request.cohortId());
Part part = findPartById(request.partId());
Team team = findTeamByIdIfPresent(request.teamId());
```
가벼운 쿼리를 미리 앞에 두어 빠르게 실패할 수 있게 했습니다.
불필요한 DB조회를 줄이는 것이 중요하다고 생각했습니다.
