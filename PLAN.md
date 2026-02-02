# 대화 요약 및 MVP 계획

## MVP 기능 범위
- 로그인: GitHub OAuth 2.0
- 문제 기록(수기 입력): 제목, 링크, 코드, 회고
- 문제 조회: 날짜순 리스트
- 시각화: 잔디(히트맵)

## 구현 순서(권장)
1) 착수 단계(1~2일)
- Git 전략: `main`/`develop`/`feature/*`
- 이슈 → PR → 리뷰 → 머지 흐름 연습
- 협업 툴: Notion/Discord/GitHub Projects

2) 백엔드 기초(3~5일)
- Spring Boot + JPA + MyBatis + MySQL 세팅
- 엔티티/DB: Member, Problem, Solution 확정
- GitHub OAuth 연동
- MVP API 1차: 문제 저장/조회(날짜순)

3) 프론트(Vue) 기본(3~5일)
- Vue 프로젝트 구조/라우팅 최소 세팅
- 로그인, 문제 등록 폼, 문제 리스트
- API 연동
- 마크다운 입력/미리보기

4) 잔디(히트맵) 1차(2~3일)
- 백엔드 통계 API: 날짜별 풀이 수
- 프론트 히트맵 시각화

5) 테스트/정리(2~3일)
- 핵심 서비스 단위 테스트
- 간단 통합 테스트(저장/조회/통계)
- Swagger/README 정리

## 협업 연습 포인트
- 기능 1개 = 이슈 1개
- PR 템플릿: 변경점/테스트/스크린샷
- 셀프 리뷰 체크리스트: API 명세, 예외 처리, DTO/Entity 분리, 프론트 상태 흐름

## 인프라 타이밍
- 초반: 로컬 + Docker 또는 로컬 MySQL
- 70% 구현 후: AWS EC2 배포 준비
- MVP 완료 직전: 배포/도메인/HTTPS
- 초기에 확정: DB 접근 방식, 환경 변수 구조
- 후반 적용: CI/CD, HTTPS/도메인
