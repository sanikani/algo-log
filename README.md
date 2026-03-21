# AlgoLog

> 알고리즘 풀이 코드와 회고를 함께 기록하는 백엔드 API

## 1. 프로젝트 개요

- 기간: 2026.02.01 ~ 진행 중
- 현재 상태: MVP 개발 중
- 목표: 문제 메타데이터, 풀이 코드, 회고를 한 번에 저장하고 조회할 수 있는 기록 API 구축

현재 구현은 "수동 풀이 기록"에 집중되어 있습니다. Chrome Extension, OAuth/JWT, 대시보드, AI 피드백은 아직 구현되지 않았고 후속 범위로 관리합니다.

## 2. 현재 구현 범위

### 도메인

- `Member`
  - 사용자 엔티티 및 저장소
- `Problem`
  - 플랫폼, 외부 문제 ID, 제목, 문제 URL, 난이도 저장
  - `(platform, externalProblemId)` 기준 중복 방지
- `Solution`
  - 문제 풀이 코드, 풀이 시간, 성공 여부, Markdown 회고 저장
  - `Member`, `Problem`과 연관관계로 연결

### API

현재 공개된 API는 `SolutionController` 기준으로 다음과 같습니다.

- `POST /api/v1/solutions`
  - 문제 메타데이터와 풀이를 함께 저장
- `GET /api/v1/solutions`
  - 내 풀이 목록 조회
- `GET /api/v1/solutions/{id}`
  - 내 풀이 상세 조회
- `PUT /api/v1/solutions/{id}`
  - 내 풀이 수정
- `DELETE /api/v1/solutions/{id}`
  - 내 풀이 삭제

모든 API는 현재 임시 방식으로 `X-Member-Id` 헤더를 사용해 사용자를 식별합니다.

### 공통 처리

- 공통 응답 래퍼: `ApiResponse`
- 공통 예외 처리: `GlobalExceptionHandler`
- API 문서화: Swagger / SpringDoc
- 테스트: JUnit 5, Mockito, Spring WebMvcTest, JPA 테스트

## 3. 기술 스택

### Backend

- Java 21
- Spring Boot 3.x
- Spring Data JPA
- MySQL 8.0
- SpringDoc OpenAPI
- JUnit 5, Mockito

### 준비 중인 기술

- MyBatis
  - `query` 패키지 골격만 존재하며 실제 조회 기능은 아직 없음
- Spring Security / OAuth / JWT
  - `security` 패키지 골격만 존재하며 인증 흐름은 아직 없음

## 4. 현재 아키텍처

현재 구현은 JPA 중심의 단순한 CRUD 구조입니다.

1. 클라이언트가 `SolutionController`로 요청 전송
2. `SolutionService`가 회원 조회, 문제 생성 또는 재사용, 풀이 저장/수정/삭제 처리
3. `ProblemRepository`, `SolutionRepository`, `MemberRepository`가 DB 접근 담당
4. `GlobalExceptionHandler`가 공통 오류 응답 처리

아직 구현되지 않은 항목:

- `QueryService`
- 대시보드용 통계 조회
- Extension 연동
- OAuth/JWT 인증
- AI 피드백 생성

## 5. 데이터 모델

### Member

- 소셜 로그인 제공자, 이메일, 닉네임, 권한 정보를 보관하는 사용자 엔티티

### Problem

- `platform`
- `externalProblemId`
- `title`
- `problemUrl`
- `difficulty`

동일한 문제는 `(platform, externalProblemId)`로 식별합니다.

### Solution

- `code`
- `timeElapsed`
- `solved`
- `memoMarkdown`
- `member`
- `problem`

풀이 수정은 `Solution` 내용만 대상으로 하고, 문제 메타데이터는 생성 시점 기준으로 재사용합니다.

## 6. 요청 예시

### 풀이 저장

```json
{
  "code": "public class Main {}",
  "timeElapsed": 123,
  "solved": true,
  "memoMarkdown": "## 회고\n- 점화식을 다시 정리해야 한다.",
  "problem": {
    "platform": "BOJ",
    "externalProblemId": "1000",
    "title": "A+B",
    "problemUrl": "https://www.acmicpc.net/problem/1000",
    "difficulty": "Bronze V"
  }
}
```

요청 헤더:

```http
X-Member-Id: 1
```

## 7. 진행 현황

### 완료

- [x] Spring Boot 프로젝트 기본 설정
- [x] 공통 응답/공통 예외 처리
- [x] `Member`, `Problem`, `Solution` 엔티티 및 저장소 구현
- [x] 문제 메타데이터를 포함한 풀이 저장 API 구현
- [x] 풀이 목록/상세 조회 API 구현
- [x] 풀이 수정/삭제 API 구현
- [x] Swagger 설정
- [x] 컨트롤러/서비스/리포지토리 테스트 추가

### 진행 예정

- [ ] 임시 `X-Member-Id` 인증 구조 정리
- [ ] OAuth/JWT 인증 도입
- [ ] MyBatis 기반 조회/통계 기능 추가
- [ ] 대시보드 API 설계
- [ ] Chrome Extension 연동
- [ ] AI 기반 코드 피드백 기능

## 8. 브랜치 및 커밋 컨벤션

### 브랜치 전략

- `main`: 배포 가능한 안정 버전
- `develop`: 통합 개발 브랜치
- `feature/#이슈번호-기능명`: 기능 개발 브랜치

### 커밋 메시지

- `feat`: 기능 추가
- `fix`: 버그 수정
- `refactor`: 구조 개선
- `docs`: 문서 수정
- `test`: 테스트 추가/수정
- `chore`: 설정 및 기타 작업
