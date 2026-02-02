# 🛠️ Project Context: AlgoLog (알고로그)

## 1. Project Overview
- **Description:** 개발자의 성장을 돕는 AI 기반 알고리즘 오답노트 & 아카이빙 플랫폼.
- **Phase:** MVP (Minimum Viable Product) 개발 단계.
- **Key Goal:** 문제 풀이 기록(CRUD), 잔디 심기(통계), GitHub 로그인 구현.

## 2. Tech Stack
- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3.4.2
- **Database:** MySQL 8.0
- **ORM (Hybrid Architecture):**
    - **Command (CUD):** Spring Data JPA (Entity, Domain Logic focus)
    - **Query (Read):** MyBatis 3.0.x (Complex Statistics, Bulk Select focus)
- **Testing:** JUnit5, Mockito
- **Build Tool:** Gradle (Groovy)
- **API Documentation:** Swagger (SpringDoc)

## 3. Architecture & Package Structure
We follow a Domain-Driven Design (DDD) inspired structure, separating Command (JPA) and Query (MyBatis).

```text
com.algolog
├── domain           # [Command/Business] JPA Entities & Logic
│   ├── member       # Member Context
│   └── solution     # Solution Context (Main Feature)
│       ├── controller
│       ├── entity   # @Entity, @Repository (JPA)
│       ├── service  # Business Logic (@Transactional)
│       └── dto      # Request/Response Records
├── query            # [Query/Read] MyBatis Mappers
│   ├── mapper       # @Mapper Interfaces
│   ├── dto          # Projection DTOs (e.g., HeatmapDto)
│   └── xml          # (resources/mapper/*.xml)
└── global           # Shared Components
    ├── config       # Security, CORS, Swagger Config
    ├── error        # Global Exception Handler, ErrorCode
    └── common       # ApiResponse, BaseEntity
4. Coding Conventions (Strict Rules)
Act as a 2-year experienced Backend Developer.

Logging: NEVER use System.out.println. Use @Slf4j and log with proper levels (INFO, WARN, ERROR).

Exception Handling:

Do not throw raw RuntimeException. Use custom exceptions (e.g., EntityNotFoundException).

Use @RestControllerAdvice for global error handling.

Return standardized ApiResponse<T> format.

DTOs: Use Java record for immutable DTOs. Never expose @Entity in Controller.

Testing:

Service layer must have Unit Tests using Mockito.

Controller layer tests using @WebMvcTest are optional for MVP.

Clean Code:

Variable/Method names must be descriptive (English).

Limit method length. If it's too long, extract methods.

Use final keyword where applicable.

5. Database Schema (Simplified ERD)
Member: member_id(PK), email, provider(OAuth), role

Problem: problem_id(PK), site, problem_no, title (Unique: site + problem_no)

Solution: solution_id(PK), member_id(FK), problem_id(FK), code(LongText), review, solved_at

6. Current Task Focus
Priority: Implement Solution domain CRUD using JPA.

Next: Implement Statistics using MyBatis.