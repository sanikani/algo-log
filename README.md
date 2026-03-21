# 🚀 Project: AlgoLog (알고로그)

> 단순 풀이 기록을 넘어, 개발자의 성장을 돕는 AI 기반 알고리즘 오답노트 플랫폼
>

## 1. 프로젝트 개요 (Overview)

### 📅 개발 기간

- **기간:** 2026.02.01 ~ (진행 중)
- **상태:** `기획 단계` → `MVP 개발`

### 🎯 기획 배경 (Why?)

기존의 알고리즘 풀이 사이트(백준, 프로그래머스)와 관리 도구들은 기능이 파편화되어 있음.

1. **BaekjoonHub:** 자동 커밋은 되지만, '회고'와 '학습 내용'을 체계적으로 정리하기 어려움.
2. **Notion/Blog:** 정리는 잘 되지만, 매번 문제 정보를 복사/붙여넣기 하는 과정이 번거로워 지속성이 떨어짐.
3. **Solved.ac:** 티어와 잔디는 보여주지만, 구체적인 코드 개선점(Code Review)은 제공하지 않음.

👉 **자동화된 수집(Extension)**과 **AI 기반의 피드백**을 결합하여, **지속 가능한 성장 시스템**을 만들고자 함.

### 👥 타겟 사용자

- 코딩테스트를 준비하며 자신의 약점을 체계적으로 관리하고 싶은 **취업 준비생**
- 단순 문제 풀이를 넘어 클린 코드와 성능 최적화를 고민하는 **주니어 개발자**

---

## 2. 핵심 기능 (Key Features)

### 🔹 Phase 1: MVP (기초 다지기)

- **사용자 관리:** GitHub OAuth 2.0 소셜 로그인.
- **문제 기록 (Manual):** 문제 링크, 제목, 코드, 난이도, 풀이 상태(성공/실패) 수동 저장.
- **마크다운 회고:** 코드 하이라이팅 및 학습 내용(Markdown) 정리 기능.
- **학습 대시보드:**
    - 일일 학습 잔디(Heatmap) 시각화.
    - 플랫폼별/난이도별 풀이 통계 차트.

### 🔹 Phase 2: 확장 (자동화 & 연결)

- **Chrome Extension 연동:**
    - 백준/프로그래머스 '제출 성공' 시 버튼 하나로 문제 정보 & 코드 자동 전송.
    - CORS 이슈 해결 및 JWT 기반 인증 통신.
- **검색 및 필터:**
    - 동적 쿼리를 활용한 상세 조건 검색 (예: "골드 난이도 중 DP 문제만").

### 🔹 Phase 3: 고도화 (AI & 최적화)

- **AI 코드 피드백 (Spring AI):**
    - 제출된 코드의 시간/공간 복잡도 분석.
    - 개선된 코드 제안 및 리팩토링 팁 제공.
- **면접관 모드:** "이 코드에 대해 면접관이 질문할 법한 예상 질문 3가지" 생성.
- **복습 알림 시스템:** 망각 곡선 이론에 따른 주기적 복습 알림 (Batch).

---

## 3. 기술 스택 (Tech Stack)

### 🛠 Backend

- **Language:** Java 21
- **Framework:** Spring Boot 3.x
- **Database:**
    - **Main:** Spring Data JPA (도메인 로직, CUD 작업)
    - **Query/Stat:** MyBatis (복잡한 통계 조회, Bulk 연산 최적화)
    - **DB:** MySQL 8.0
- **API Doc:** Swagger (SpringDoc)
- **Test:** JUnit5, Mockito

### 💻 Frontend (Client)

- **Web:** Vue.js
- **Extension:** HTML/CSS/JS

### ☁️ Infrastructure & Tools

- **Server:** AWS EC2 (Free Tier)
- **CI/CD:** GitHub Actions
- **Cooperation:** Git, Notion, Discord

---

## 4. 시스템 아키텍처 (Architecture)

1. **Client (Chrome Extension):** DOM 파싱 → API 서버로 JSON 전송.
2. **Server (Spring Boot):**
    - `SolutionController`에서 요청 접수.
    - `CommandService`(JPA)가 데이터 검증 및 저장.
    - `QueryService`(MyBatis)가 대시보드용 통계 데이터 조회.
3. **Database:** `Member` - `Solution` - `Problem` 구조로 정규화된 설계.

### 현재 인증 처리 방식

- 현재 MVP API는 임시로 `X-Member-Id` 헤더를 사용해 사용자를 식별합니다.
- 컨트롤러는 헤더를 직접 받지 않고 `@CurrentMemberId`를 통해 현재 사용자 ID를 주입받습니다.
- 이후 JWT 또는 OAuth Principal 기반 인증을 도입할 때는 컨트롤러 시그니처를 바꾸지 않고 argument resolver 구현만 교체하는 것을 목표로 합니다.

---

## 5. 데이터베이스 설계 (ERD)

![image.png](attachment:486e2e6b-75e2-45fe-b7f4-aa7dffc69d34:image.png)

- **Members:** 사용자 정보 및 소셜 로그인 연동.
- **Problems:** 문제 메타 데이터 (중복 방지).
- **Solutions:** 사용자의 풀이 코드, 회고, AI 피드백 저장.

---

## 6. 개발 일정 (Roadmap)

### ✅ 프로젝트 세팅 & 백엔드 코어

- [ ]  Spring Boot 프로젝트 생성 및 의존성 설정 (JPA + MyBatis)
- [ ]  GitHub Repository 연동 및 Git Flow 전략 수립
- [ ]  DB 설계 (ERD) 및 Entity/Mapper 구현
- [ ]  문제 저장/조회 기본 API (CRUD) 구현 및 테스트

### ✅데이터 수집 & 프론트엔드

- [ ]  Chrome Extension 개발 (DOM 파싱 및 서버 전송)
- [ ]  CORS 설정 및 JWT 인증 연동
- [ ]  기본 UI (목록, 상세, 작성 페이지) 구현

### ✅ 시각화 & AI 도입

- [ ]  대시보드 잔디(Heatmap) 구현 (MyBatis 집계 쿼리 활용)
- [ ]  Spring AI 연동 및 프롬프트 엔지니어링 테스트
- [ ]  배포 (AWS EC2) 및 시연 영상 제작

---

## 7. 컨벤션 (Ground Rules)

### 📌 Commit Message

- `feat`: 새로운 기능 추가
- `fix`: 버그 수정
- `refactor`: 코드 리팩토링 (기능 변경 없음)
- `docs`: 문서 수정
- `chore`: 빌드 설정, 패키지 매니저 설정 등

### 📌 Branch Strategy

- `main`: 배포 가능한 안정 버전
- `develop`: 개발 중인 버전
- `feature/기능명`: 단위 기능 개발 브랜치
