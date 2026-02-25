
# System Design Architecture

## 현재 구조 분석

지금까지 구현한 시스템은 전형적인 Spring Boot 모놀리식 구조입니다.

Layer Architecture (Controller - Service - Repository - Domain)로 구성했고,

H2 In-memory Database를 사용한 단일 서버 구조입니다.

## 이상적인 프로덕션 아키텍처

실제 운영 환경을 고려했을 때, 이런 구조가 이상적이지 않을까 생각했습니다.

![System Architecture](/docs/image/SDA.png)

---

## 핵심 개선 사항

### 1. Redis 활용

#### - QR 코드 상태 관리

현재는 DB에 `expiresAt`을 저장하고 매번 조회하는 방식입니다.

Redis에 TTL을 설정하면 자동으로 만료되어서 더 효율적일 것 같습니다.

```
Key: qr:session:{sessionId}
Value: {hashValue}
TTL: 24시간
```

#### - 출석 통계 캐싱

지금은 매번 집계 쿼리를 실행하고 있습니다.

Redis에 5분 정도 캐싱해두면 조회 성능이 개선될 것 같습니다.

```
Key: attendance:stats:{sessionId}
Value: {"present": 10, "absent": 2, ...}
TTL: 5분
```

---

### 2. DB 최적화

#### - Read Replica 분리

Write 작업(출석 체크, 회원 등록)은 Primary DB에서 처리하고,

Read 작업(대시보드 조회, 통계)은 Replica DB로 분산시키면 부하를 줄일 수 있을 것 같습니다.

#### - Connection Pool

HikariCP 설정을 최적화하면 동시 접속자가 증가해도 안정적으로 대응할 수 있을 것 같습니다.

---

### 3. 확장성

#### - Horizontal Scaling

Stateless API 서버로 구성하면 자유롭게 Scale-out이 가능합니다.

Load Balancer로 트래픽을 분산시키면 됩니다.

#### - QR 출석 체크 병목 해결

세션 시작 시간에 트래픽이 집중될 것 같습니다.

Redis 기반 락으로 동시성을 제어하고, 페널티 계산은 비동기로 처리하면 좋을 것 같습니다.

---

### 4. 보안

#### - API 보호

회원별로 분당 100회 정도 Rate Limiting을 걸고,

CORS 설정과 Prepared Statement로 SQL Injection을 방어하면 보안이 두터워질 것 같습니다

---

### 5. 모니터링

#### --핵심 지표 (grafana + prometheus)

QR 출석 체크 성공률, API 응답 시간(P95, P99), DB 슬로우 쿼리, 에러율 같은 지표들을 추적합니다.

#### - 알림

시스템 장애가 발생하면 Slack으로 알림을 보내고,

출석률이 급락하면 관리자에게 알림을 보내는 식으로 운영하면 좋을 것 같습니다.

---

## 단계별 보완 계획

### 1. 안정화

먼저 H2를 PostgreSQL로 전환하고,

기본 모니터링과 CI/CD 파이프라인을 구축합니다.

### 2. 성능 개선

Redis를 도입해서 QR 상태와 세션을 관리하고,

Read Replica를 구성하고 인덱스를 최적화합니다.

### 3. 확장

Kubernetes 기반 Auto Scaling을 적용하고,

CDN을 붙이고, 추후에는 Multi-Region도 고려해볼 수 있을 것 같습니다.

## 비용 효율적인 대안

스타트업 초기라면 AWS 기준으로 이 정도 구성이면 충분할 것 같습니다.

- ECS Fargate (API 서버 2대)
- RDS PostgreSQL (Single AZ)
- ElastiCache Redis (Small Instance)
- CloudFront (CDN)
- Route53 (DNS)

월 예상 비용은 $100-200로 예상합니다.
동아리 수준에서 운영하기에는 부담스러운 금액일 것 같다는 염려가 들긴합니다.
그래도 이 정도면 현실적이면서도 확장 가능한 구조가 될 것 같습니다.
