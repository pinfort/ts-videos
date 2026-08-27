# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

TS抜き録画データを処理・管理するためのアプリケーション ([README.md](README.md)). Two independently-deployed pieces:

- `backend/` — Kotlin/Spring Boot, Gradle multi-module ([backend/README.md](backend/README.md))
- `frontend/` — Next.js/TypeScript, talks to `manager:api` over HTTP ([frontend/README.md](frontend/README.md))

## Commands

### Backend (run from `backend/`)

```bash
./gradlew build                              # build all modules
./gradlew test                               # run all tests (needs Docker; Testcontainers spins up MariaDB)
./gradlew core:test --tests "me.pinfort.tsvideos.core.SomeSpec"   # run a single test class
./gradlew ktlintCheck                        # lint
./gradlew ktlintFormat                       # auto-fix lint
./gradlew manager:api:bootRun                # run the API locally (needs docker-compose.test.yml services)
./gradlew processor:console:bootRun --args="<path-or-dir>"   # run the processing pipeline CLI (tvpcli); pass -d/--dry-run to skip writes
```

Local DB/NAS dependencies: `docker compose -f docker-compose.test.yml up -d` from the repo root (see [backend/README.md](backend/README.md) for env var overrides).

### Frontend (run from `frontend/`)

```bash
pnpm install
pnpm dev                    # dev server
pnpm build                  # production build
pnpm typecheck               # tsc --noEmit
pnpm lint                   # eslint
pnpm test                   # vitest run (all)
pnpm test:watch              # vitest watch mode
pnpm test -- path/to/file.test.tsx   # run a single test file
pnpm coverage                # vitest run --coverage
```

`NEXT_PUBLIC_API_BASE_URL` controls the backend URL, defaulting to `http://localhost:8080`.

## Architecture

### Backend module graph

- `core` — no internal deps; everything else depends on it
- `manager:infrastructure` — depends on `core`
- `manager:api` — depends on `core` + `manager:infrastructure`
- `manager:console` — depends on `core` + `manager:infrastructure`
- `processor:infrastructure` — depends on `core`
- `processor:console` — depends on `core` + `processor:infrastructure`

- **`core`** owns everything shared: domain models (`core/domain`), MyBatis DTOs/mappers/converters for DB access (`core/external/database`), the Samba/NAS client (`core/external/samba`), shell execution (`core/external/shell`), wrappers around external recording tools (`core/external/tool` — `DropChkClient`, `TsSplitterClient`, `AmatsukazeAddTaskClient`, `DurationProbeClient`), reusable pipeline helpers (`core/component` — e.g. `CompressComponent`, `DirectoryNameComponent`, `MainSplittedFileFinderComponent`), and **`command`** classes (e.g. `ProgramCommand`, `ExecutedFileCommand`, `SplittedFileCommand`, `CreatedFileCommand`) that are the actual business-logic layer other modules call into. New cross-cutting logic (DB queries, NAS/shell/tool interaction, domain rules) belongs here, not in `manager`/`processor`.
- **`manager:api`** is a thin Spring MVC layer (`controller`/`response`/`exception`) over `core`'s commands — controllers should stay free of business logic.
- **`manager:console`** is a Clikt-based CLI (`commands/`) over the same `core` commands, for operator scripts (search/get/modify/delete).
- **`processor:infrastructure`** turns raw recordings into the state `manager` can track. `pipeline/FileProcessingPipeline` is a 4-stage pipeline (DropChk → TsSplitter → compress + upload to NAS → Amatsukaze add-task); each stage rolls back itself and every earlier stage in reverse order before rethrowing on failure. `pipeline/PathProcessingRunner` expands a file/directory into `.m2ts` files and runs the pipeline per file, reporting failures to Slack via `external/slack/SlackClient` (`SLACK_WEBHOOK_URL`, no-op when unset).
- **`processor:console`** is a Clikt CLI (`tvpcli`, `commands/ProcessCommand`) over `PathProcessingRunner`; takes one or more paths plus `-d`/`--dry-run`, and renders compress/upload progress bars via `display/ProgressPrinter`. Runs as a non-web Spring Boot app (`CommandLineRunner`).
- MyBatis is used directly (mapper interfaces + XML/annotations under `core/external/database`), not JPA/Hibernate.
- Tests use Kotest (not JUnit assertions) + MockK (not Mockito — explicitly excluded in `build.gradle.kts`) + Testcontainers-backed MariaDB for anything touching the DB. The Testcontainers init script lives at `core/src/test/resources/ddl/01_create_database.sql`.

### Frontend

- Next.js App Router; pages under `frontend/app/**`, shared UI under `frontend/components/`.
- All backend calls go through `frontend/lib/api/` (`client.ts` has the shared `fetchJson`/`ApiError` helpers; `programs.ts`/`video.ts` wrap specific endpoints). Add new backend calls there rather than calling `fetch` directly from components/pages.
- The video playback/download page fetches video bytes directly from the backend in the browser (not proxied through Next.js), so `NEXT_PUBLIC_API_BASE_URL` must be a browser-reachable URL.
- Every component/page/lib module has a co-located `*.test.tsx`/`*.test.ts` file (Vitest + Testing Library + jsdom, configured in `vitest.config.ts`). Follow that co-location pattern for new files.
