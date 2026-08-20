# PropertyHub — Current Implementation Log (STEP-01 through STEP-15)

## Purpose

This document is a complete, detailed record of everything implemented so far,
written so that a fresh session (with no memory of this conversation) can pick
up the project without re-deriving context. It covers, per STEP: what was
asked, what was planned, what was built, every issue hit during validation and
its fix, and the final validated outcome.

This is a supplement to `CLAUDE.md` (process rules) and `01-development-plan.md`
(the approved STEP sequence) — it is the record of what actually happened
when that plan was executed.

---

# 1. Environment & Tooling Reference

## Target environment (fixed, from the STEP-01 compatibility check)

```text
JDK / Java:       21
Maven:            3.9.16
PostgreSQL:       17.6.1
Node.js:          25.3.0
npm:              11.6.2
```

## Actual local machine state (may differ from target — always prefer target)

```text
Default JDK on PATH: Java 25 (D:\java\jdk-25)
JDK 21 available at: D:\java\jdk-21.0.11  <-- use this for all backend builds
Maven: D:\maven (Apache Maven 3.9.14 — close enough to target, not identical)
Node: v25.8.2 / npm 11.12.1 (close to target)
PostgreSQL 17.11 installed at "C:\Program Files\PostgreSQL\17"
  - superuser: postgres / postgres (verified working)
  - pgvector extension 0.8.3 available (not activated per-db until CREATE EXTENSION runs)
```

**Every backend `mvn` command in this project must be run with:**
```bash
export JAVA_HOME=/d/java/jdk-21.0.11
export PATH="$JAVA_HOME/bin:$PATH"
```
Omitting this uses Java 25 by default, which still happens to compile/run this
codebase, but target-environment fidelity requires JDK 21 explicitly.

## Final verified version matrix (from STEP-01 compatibility check)

```text
Spring Boot            4.1.0
Spring Cloud (BOM)      2025.1.2 (Oakwood)
Spring AI               2.0.0 (GA, for Spring Boot 4.x — NOT 1.1.x, which only supports Boot 3.5)
springdoc-openapi        3.0.3 (v3.x line, for Boot 4.x — NOT v2.8.x, which is Boot 3.x only)
PostgreSQL JDBC driver   42.7.9
pgvector extension       0.8.3 (already installed in the local PG17 instance)
React                    19.2.x
Vite                     7.x
Vitest                   3.2+
Node.js                  25.3.0 target (25.8.2 actual — both satisfy Vite/Vitest's floor)
```

## Databases created so far (all on the local PostgreSQL 17 instance, postgres/postgres)

```text
propertyhub_auth      — auth-service (users)
propertyhub_property  — property-service (properties)
propertyhub_ai        — ai-service (conversations, chat_messages, vector_store)
```

## Service ports

```text
eureka-server    8761
api-gateway      8080
auth-service     8081
property-service 8082
ai-service       8083
admin-server     8084
```

## AI provider

- Provider: **OpenAI**, via a **Capgemini corporate gateway** (not the public OpenAI API directly) — this is a corporate-laptop environment with Privacy & Data Protection constraints.
- `spring.ai.openai.base-url` = `https://openai.generative.engine.capgemini.com`
- Chat model id format on this gateway: **`openai.gpt-5-mini`** (prefixed) — the bare OpenAI model id (`gpt-4o-mini`) returned errors initially; corrected after the user provided the working name.
- Embedding model: **required a different override** from the chat model — `spring.ai.openai.embedding.base-url` = `https://openai.generative.engine.capgemini.com/v1` (note the `/v1` suffix, unlike the chat base-url) and embedding model id is **unprefixed**: `text-embedding-3-small` (no `openai.` prefix, unlike the chat model). This asymmetry was discovered live via a 400 error and user correction — do not "fix" it to be consistent, it is correct as-is for this gateway.
- `OPENAI_API_KEY` is a Windows **User environment variable** (not Machine/System) — an already-running shell will NOT see it until re-exported manually, or a brand-new shell is started after it was set. In this session, the key was read once via `[Environment]::GetEnvironmentVariable("OPENAI_API_KEY", "User")` in PowerShell and then `export`ed manually into each Bash session needed for live AI validation. It is **never** written to any file in the repo — `application.yml` only references `${OPENAI_API_KEY}` with no default (a missing key must fail loudly, unlike JWT's dev-safe default).

## Live-validation working pattern (used throughout)

For every STEP needing a running service:
1. Start service(s) via `mvn spring-boot:run > /d/PropertyHub/.<service>-run.log 2>&1` with `run_in_background: true`.
2. Poll the log file for `Started <X>Application` (or `APPLICATION FAILED TO START`) before proceeding.
3. Run `curl` validation commands.
4. Find the PID via `netstat -ano | grep ":<port>" | grep LISTENING` and `taskkill //F //PID <pid>` to stop.
5. Delete the temp log file.
6. **Known gotcha:** `TaskStop` (used to stop a background Bash tool task) kills the `mvn` wrapper process but **not** the forked JVM child process — this leaves the port held, causing the *next* `spring-boot:run` to fail with "Port already in use". Always verify via `netstat` and `taskkill` the actual JVM PID, not just rely on stopping the background task.
7. A DNS instability issue was found and fixed early (see STEP-08) — `eureka.instance.prefer-ip-address: true` is set on all 5 Eureka-registering services (everything except eureka-server itself) to avoid it recurring.

---

# 2. Documentation & Process Notes

- The user follows the `CLAUDE.md` PLAN → approval → BUILD → approval → TEST → result lifecycle strictly, one STEP at a time.
- Every STEP in this log went through that exact cycle; only the *outcome* is summarized below, not the full back-and-forth text (that's preserved in the actual conversation transcript if ever needed — this doc is the durable reference for future sessions).
- **STEP-14's actual scope was initially incomplete** — the assistant proposed a plan missing two literal bullet points from `01-development-plan.md` ("Switch to Lombok", "ModelMapper Bean Approach"). The user caught this by asking for a recheck against the plan file before approving. **Lesson for future STEPs: always re-read the actual STEP text from the plan file fresh, do not rely on memory/summary of it.**
- Security has an **intentionally open, explicitly flagged gap**: only `auth-service` has JWT/Spring Security wired. `property-service`, `ai-service`, and `api-gateway` have no authentication on their business endpoints — this was a deliberate scope decision made at STEP-06 (confirmed via `AskUserQuestion`, user chose "Unsecured for now") and re-confirmed at STEP-08 and STEP-14. **This must be resolved in a dedicated future STEP before the project is considered complete** — do not silently patch it piecemeal.

---

# 3. STEP-by-STEP Log

## STEP-01 — Workspace and Build Foundation

**Objective:** Independent Maven projects (not multi-module) for all 6 backend services + 2 React/Vite frontends, all buildable/runnable, no business logic yet.

**Preceded by a mandatory compatibility recheck** (user explicitly required this before implementation): verified Java 21 + Spring Boot 4.1.0 + Spring Cloud 2025.1.2 + Spring AI 2.0.0 + springdoc 3.0.3 + PostgreSQL 17.6.1 + pgvector 0.8.x + React 19/Vite 7/Vitest 3.2 all mutually compatible. Key finding: Spring AI has two incompatible lines — 1.1.x only works with Spring Boot 3.5.x (EOL June 2026), 2.0.0 GA (released June 12, 2026) is required for Spring Boot 4.x. Verdict: READY FOR STEP-01.

**Created:**
- `eureka-server/`, `api-gateway/`, `auth-service/`, `property-service/`, `ai-service/`, `admin-server/` — each an independent Maven project: `pom.xml` (spring-boot-starter-parent 4.1.0, java.version 21), a bare `*Application.java`, `application.yml` (port + spring.application.name + actuator health,info), a context-load test.
- `frontend/`, `admin-panel/` — Vite + React 19 (plain JS, not TS) apps with Vitest configured inline in `vite.config.js`, minimal `App.jsx`/`main.jsx`.

**Key decisions:**
- Independent Maven projects (no parent/multi-module POM), per explicit user instruction.
- API Gateway uses the new **WebMVC-flavored** Spring Cloud Gateway (`spring-cloud-gateway-server-webmvc`), not the classic reactive/WebFlux one — keeps the whole stack on the servlet model, avoids mixing reactive+imperative.
- Admin Server monitors other services via **Eureka-based auto-discovery**, not a per-service `spring-boot-admin-starter-client` dependency — this was verified working end-to-end in STEP-14.
- Frontend apps: plain JavaScript (not TypeScript), Vitest test env via `jsdom`.

**Issues & fixes during validation:**
- None — all 8 modules built and tested clean on the first `mvn clean verify` / `npm install && build && vitest` pass.

**Validation result:** All 6 backend `mvn clean verify` → BUILD SUCCESS. Both frontends installed/built/tested clean under Node 25.

---

## STEP-02 — Eureka Server

**Objective:** Runtime verification that Eureka Server (already functionally complete from STEP-01) actually starts, serves its dashboard, and accepts client registrations.

**No code changes** — STEP-01's implementation already satisfied the requirements. This STEP was pure runtime verification.

**Validation performed:**
- Started `eureka-server`, confirmed dashboard HTML at `http://localhost:8761/` (200).
- Started `auth-service` (already configured with `eureka.client.service-url.defaultZone`), confirmed it registered (`registration status: 204`) and appeared via `GET /eureka/apps/AUTH-SERVICE` with `status: UP`.

**Issues:** None.

---

## STEP-03 — API Gateway

**Objective:** Wire real routing (`/api/auth/**`, `/api/properties/**`, `/api/ai/**` → `lb://` targets), CORS, and basic request logging. JWT-based security explicitly deferred (Auth Service doesn't issue JWTs yet at this point).

**Created:**
- `config/CorsConfig.java` — standard Spring MVC `WebMvcConfigurer.addCorsMappings`, allowing `http://localhost:5173` and `:5174` (Vite dev-server defaults for `frontend`/`admin-panel`).
- `filter/RequestLoggingFilter.java` — `OncePerRequestFilter` logging method/path in, method/path/status out.

**Modified:**
- `pom.xml` — added `spring-cloud-starter-loadbalancer` (required for `lb://` resolution; not pulled in transitively by the Eureka client starter).
- `application.yml` — added `spring.cloud.gateway.server.webmvc.routes` for the three services.

**Key research finding:** the declarative route YAML property was renamed in Spring Cloud 2025.0+; the correct path is `spring.cloud.gateway.server.webmvc.routes` (not the deprecated `spring.cloud.gateway.mvc.routes`, and not the webflux-only `spring.cloud.gateway.routes`). Also: Spring Cloud Gateway Server WebMVC has **no documented `globalcors` YAML property** the way the reactive gateway does — hence the standard Spring MVC `WebMvcConfigurer` approach instead.

**Issues & fixes:** None during build. Live validation (`GET /api/auth/ping` through the gateway) took ~21 seconds on the **first** request (client-side load-balancer cache warm-up against a Eureka-resolved instance) — this turned out to foreshadow a real DNS issue fully diagnosed and fixed in STEP-08.

**Validation result:** `mvn clean verify` passed. Live: gateway correctly proxied to `auth-service` (404 from auth-service itself, proving the hop worked — no `/auth` controller existed yet), CORS preflight returned the correct `Access-Control-Allow-Origin` header, request logging fired.

---

## STEP-04 — Auth Service: Registration and User Model

**Objective:** `User`/`Role` entity, PostgreSQL persistence, `POST /api/auth/register`, request/response DTOs, validation, exception handling. No login/JWT yet (that's STEP-05).

**Clarification needed and resolved:** PostgreSQL connection credentials. `AskUserQuestion` asked whether to use default `postgres/postgres` on a new `propertyhub_auth` database or have the user provide exact credentials — user chose the default, which was then verified working via `psql`.

**Created:** `entity/User.java`, `entity/Role.java` (enum: BUYER, AGENT, ADMIN), `repository/UserRepository.java`, `dto/request/RegisterRequest.java`, `dto/response/UserResponse.java`, `service/AuthService.java`, `controller/AuthController.java`, `exception/{GlobalExceptionHandler,UserAlreadyExistsException,ErrorResponse}.java`, `config/PasswordEncoderConfig.java` (BCrypt bean).

**Modified:** `pom.xml` (data-jpa, validation, `spring-security-crypto` **only** — not the full `spring-boot-starter-security`, since that would auto-lock every endpoint before real security config exists), `application.yml` (datasource + JPA).

**Key decision:** password encoding needed now, but full Spring Security deferred to STEP-05 — used the standalone `spring-security-crypto` artifact just for `BCryptPasswordEncoder`.

**Issue & fix (one cycle):**
- **Problem:** Spring Boot 4.1 **removed `@MockBean`/`@SpyBean` entirely** (deprecated since 3.4). `AuthControllerTest` used `@MockBean`, causing a compile error (`package ... does not exist`).
- **Fix:** replaced with `@MockitoBean` from `org.springframework.test.context.bean.override.mockito` (the direct 4.x replacement). This pattern recurred in every subsequent STEP's new test files — always use `@MockitoBean`, never `@MockBean`, on this Spring Boot version.

**Validation result:** `mvn clean verify` → 7/7 tests. Live: registration returns 201 with a password-free `UserResponse`; duplicate email → 409; invalid payload → 400; DB row confirmed to store only a BCrypt hash (`$2a$10$...`), never plaintext.

---

## STEP-05 — Auth Service: Login, JWT and RBAC

**Objective:** Login, JWT issuance/validation, protected `GET /api/auth/me`, `ROLE_*` authority infrastructure for later services to build on.

**Created:** `dto/request/LoginRequest.java`, `dto/response/LoginResponse.java`, `service/JwtService.java` (HS256 via `jjwt` 0.13.0), `security/{SecurityConfig,JwtAuthenticationFilter,JwtAuthenticationEntryPoint}.java`, `exception/{InvalidCredentialsException,ResourceNotFoundException}.java`.

**Modified:** `pom.xml` (`spring-boot-starter-security`, `jjwt-api`/`jjwt-impl`/`jjwt-jackson` 0.13.0), `application.yml` (`jwt.secret` env-var with a **dev-only default**, `jwt.expiration-ms` default 3600000), `GlobalExceptionHandler` (401/404 handlers).

**Design decision:** login credential verification done **manually** in `AuthService` (fetch by email, `passwordEncoder.matches(...)`) rather than wiring a full `AuthenticationManager`/`DaoAuthenticationProvider`/`UserDetailsService` chain — avoids infrastructure for what's a two-line check. `JwtAuthenticationFilter` reads claims directly from the token (stateless, no DB hit per request).

**Issues & fixes (three cycles on this STEP — the most iteration of any single STEP):**
1. **`@WebMvcTest` pulls in `Filter`-type beans automatically.** Once `spring-security` was added, `JwtAuthenticationFilter` (implements `Filter` via `OncePerRequestFilter`) got auto-included in the `@WebMvcTest(AuthController.class)` slice, requiring `JwtService` — not part of that slice — causing `NoSuchBeanDefinitionException`. **Fix:** added `@MockitoBean JwtService jwtService;` to the test so the dependency resolves (never actually invoked, since `addFilters=false` was also set).
2. **Mockito field-initializer ordering bug** (recurred in almost every later STEP too): `private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);` as a field initializer runs *before* `MockitoExtension` injects `@Mock` fields, so `jwtService` was `null` at construction time. **Fix:** move construction into a `@BeforeEach` method. **This is now a standing pattern to check for in any new Mockito test — never construct the system-under-test as a field initializer when it depends on `@Mock` fields.**
3. **`@WebMvcTest` + `addFilters=false` + `Authentication` method parameter is fundamentally incompatible.** Spring MVC resolves a controller's `Authentication`/`Principal` parameter from `HttpServletRequest.getUserPrincipal()`, which is itself populated by the security filter chain — with filters disabled, it's always `null`, causing an NPE in the `/me` endpoint test regardless of `@WithMockUser` or `SecurityMockMvcRequestPostProcessors.user(...)` (both were tried and both failed for this specific reason). **Fix:** abandoned trying to test this via `@WebMvcTest`; removed that one test case, added an equivalent plain-Mockito unit test (`AuthServiceTest.getCurrentUserReturnsUserResponse`) for the underlying logic, and relied on the **live** curl-based validation (which exercises the real, fully-wired filter chain) to prove `/me` actually requires and accepts a JWT end-to-end. **Lesson: don't fight framework slice-testing limitations around security filters — test the logic in isolation and verify the wiring live.**

**Validation result:** `mvn clean verify` → 21/21 tests (after the three fixes). Live: register → login → `/me` with real token → 200; `/me` with no token → 401 clean JSON; `/me` with garbage token → 401.

---

## STEP-06 — Property Service Foundation

**Objective:** `Property` entity, PostgreSQL persistence, full CRUD, DTOs, validation, exception handling.

**Clarification needed and resolved (`AskUserQuestion`):** should property-service have JWT security wired in this STEP, given the dev plan doesn't list "security" under STEP-06's bullets? User chose **"Unsecured for now"** — this is the origin of the still-open security gap noted in §2 above. `agentId` is supplied directly in the request body (no JWT context to derive it from).

**Created:** `entity/{Property,PropertyType,Furnishing}.java`, `repository/PropertyRepository.java`, `dto/request/{CreatePropertyRequest,UpdatePropertyRequest}.java`, `dto/response/PropertyResponse.java`, `mapper/PropertyMapper.java` (originally a static utility — later refactored to a `@Component` in STEP-14), `service/PropertyService.java`, `controller/PropertyController.java`, `exception/{GlobalExceptionHandler,ResourceNotFoundException,ErrorResponse}.java`.

**Modified:** `pom.xml` (data-jpa, validation, postgresql driver), `application.yml` (datasource for new `propertyhub_property` db).

**Issue & fix (caught by the assistant's own live inspection, not a build failure):**
- **Problem:** the PLAN promised DB-level `CHECK (price > 0)`, `CHECK (bhk > 0)`, `CHECK (area > 0)` constraints, matching `02-validation-contract.md`. After building, live inspection of the actual schema (`\d properties` in psql) showed these constraints were **missing** — Hibernate's `ddl-auto=update` does not translate Bean Validation's `@Positive` annotation into DDL; only `@NotNull`/`@Column(nullable=false)` and `@Enumerated` (surprisingly, Hibernate 7 auto-generates enum-value CHECK constraints) actually produce schema constraints.
- **Fix:** added explicit **JPA 3.2 `@CheckConstraint`** annotations via `@Table(check = {...})` (the modern, portable API, since the project is on Jakarta Persistence 3.2 / Hibernate 7 — used this instead of the older Hibernate-specific `@Check` annotation). Since the table already existed without the constraints, the database had to be **dropped and recreated** for `ddl-auto=update` to build the schema fresh with them included (a running `ddl-auto=update` app doesn't retroactively add table-level constraints to existing tables).

**Validation result:** `mvn clean verify` → 17/17 tests. Live: full create→get→update→delete cycle; 404 on deleted/nonexistent; 400 on invalid payload; **and** the corrected schema was re-verified via psql showing all 3 `CHECK` constraints plus Hibernate's auto-generated enum constraints.

---

## STEP-07 — Property Search and Filtering

**Objective:** `GET /api/properties` with combinable filters (city, bhk, price range, area range, propertyType, furnishing, parking).

**Design decision:** individual `@RequestParam(required = false)` args on the controller + a single JPQL query using the `(:param IS NULL OR field = :param)` idiom, rather than Spring Data Specifications or a `@ModelAttribute`-bound record — kept deliberately simple, matches "keep search implementation straightforward" from the dev plan. Cross-field validation (`minPrice <= maxPrice`, `minArea <= maxArea`, non-negative bounds, `bhk > 0`) done manually in the service layer.

**Created:** `exception/InvalidSearchException.java`. **Modified:** `PropertyRepository` (new `search(...)` JPQL query method), `PropertyService`, `PropertyController`, `PropertyMapper` (added `toSummary`), `GlobalExceptionHandler`.

**Issue & fix (one cycle, live-only — not caught by unit tests since they mock the repository):**
- **Problem:** `GET /api/properties` with `city` omitted → `500`, root cause `org.postgresql.util.PSQLException: ERROR: function lower(bytea) does not exist`. In `(:city IS NULL OR LOWER(p.city) = LOWER(:city))`, when `city` is `null`, the PostgreSQL JDBC driver sends that bind parameter as an untyped value, and Postgres can't resolve which `LOWER(...)` overload applies during query *planning* — even though the `IS NULL OR` clause would short-circuit it at runtime, Postgres still type-checks the whole expression up front.
- **Fix:** explicit cast in the JPQL: `LOWER(CAST(:city AS string))` — forces Postgres to always see it as text regardless of null-ness. **This is a general pattern to remember: any `(:param IS NULL OR someFunction(:param))` JPQL idiom involving a function call on the nullable parameter needs an explicit `CAST` if the parameter can genuinely be null, or PostgreSQL's query planner will error before the null-check ever executes.**

**Validation result:** `mvn clean verify` → 25/25 tests. Live: no-filter search returns all; `city+bhk` combo, price-range combo both return correctly narrowed subsets against real seeded data; invalid range → 400 before touching the DB.

---

## STEP-08 — OpenFeign Property Communication

**Objective:** `PropertyFeignClient` in ai-service (getProperty, searchProperties, getPropertiesByIds) calling property-service via Eureka discovery + client-side load balancing.

**Gap closed as part of this STEP:** `getPropertiesByIds()` had no corresponding property-service endpoint — added `GET /api/properties/batch?ids=1,2,3` (`PropertyService.getByIds`, `PropertyRepository.findAllById`) since STEP-08 needed something real to call.

**Created (ai-service):** `dto/{PropertyDto,PropertySummaryDto}.java` (local copies, not shared JARs — independent services), `client/PropertyFeignClient.java`, `service/PropertyClientService.java` (catches `FeignException.NotFound` → `PropertyNotFoundException`, other `FeignException` → `AiServiceException`), `exception/{GlobalExceptionHandler,ErrorResponse,PropertyNotFoundException,AiServiceException}.java` — this is ai-service's **first** exception infrastructure.

**Modified:** `pom.xml` (openfeign, loadbalancer, `feign-okhttp`), `AiServiceApplication` (`@EnableFeignClients`).

**Validation approach note:** ai-service had no HTTP endpoint of its own yet at this STEP (no controller — that's STEP-09), so a `curl`-based live check wasn't possible. Solution: a JUnit test class **deliberately named without the `Test`/`Tests`/`TestCase` suffix** (`PropertyFeignClientLiveCheck`) — Maven Surefire's default discovery glob excludes it automatically from `mvn verify`, but it's invokable explicitly via `mvn test -Dtest=PropertyFeignClientLiveCheck` once real services are running. **This is a reusable pattern for any future "live-only, not part of CI" check.**

**Major issue & fix (the most involved diagnostic session of the whole project so far — 3 attempted fixes, only the 3rd correct):**
- **Symptom:** the live check consistently failed with `feign.RetryableException: Connect timed out`, even though Eureka discovery and LoadBalancer selection both succeeded instantly and `curl` could reach the same host/port trivially from the same machine.
- **Attempt 1 (wrong):** assumed it was a slow-first-connection timing issue (STEP-03's gateway took 21s once) — raised Feign's connect/read timeouts to 30s. Did not help (failed after the full 30s instead of 10s).
- **Attempt 2 (wrong):** assumed it was `HttpURLConnection`-specific (Feign's default transport) — swapped to OkHttp (`feign-okhttp` + `spring.cloud.openfeign.okhttp.enabled=true`). Did not help.
- **Root-cause diagnosis (user explicitly required stopping guesswork and diagnosing layer-by-layer first):** wrote a temporary diagnostic JUnit test (`FeignDiagnosticCheck`, later deleted) that directly inspected `DiscoveryClient.getInstances()`, `LoadBalancerClient.choose()`, then did a **raw `java.net.Socket` connect** with explicit timing, bypassing Feign/LoadBalancer entirely. This revealed: `InetAddress.getByName("SuperAP.mshome.net")` (the hostname all services register under, via Eureka's default `prefer-ip-address=false`) returned **`172.27.16.1`** — a **different IP** than the one property-service was actually bound to (**`172.24.96.1`**, confirmed via earlier successful `curl` calls and Eureka's own `ipAddr` field). Both a raw socket connect to the hostname AND to the freshly-resolved IP timed out identically. This is a Windows virtual-adapter (Hyper-V/Mobile-Hotspot NAT) DNS-instability issue — the hostname resolves to a stale/wrong address depending on when the OS resolver is queried, unrelated to Feign, LoadBalancer, or any HTTP client choice.
- **Fix (3rd attempt, correct):** set **`eureka.instance.prefer-ip-address: true`** — registers services by IP address instead of the unstable hostname. Applied to **all 5 Eureka-registering services** (not just property-service) since this is a systemic risk for any future inter-service call, not specific to this one path. After restarting property-service to re-register with the new setting, all 4 live-check tests passed in under 6 seconds.
- **Lesson for the future:** if a Eureka-discovered service call times out despite discovery/load-balancer resolution succeeding quickly, suspect hostname DNS instability before touching HTTP client libraries or timeouts — verify with a raw `Socket` connect test to isolate the layer, and check whether `prefer-ip-address` is set.

**Validation result:** `mvn clean verify` passed independently for both services. Live: `getProperty`/`searchProperties`/`getPropertiesByIds` all succeed against real data; a nonexistent id correctly maps to `PropertyNotFoundException` (404).

---

## STEP-09 — AI Chat Foundation

**Objective:** Spring AI `ChatClient`/`ChatModel` wired to the OpenAI provider, `POST /api/ai/chat`.

**Clarification needed and resolved (`AskUserQuestion`):** which AI provider? User chose **OpenAI** (over local Ollama or Anthropic). `OPENAI_API_KEY` was not set in the environment at the time — user was walked through setting it as a Windows User env var and it was read once via PowerShell registry query, then exported into the Bash session manually for live testing only (see §1 above).

**Created:** `config/AiConfig.java` (`ChatClient` bean), `dto/request/ChatRequest.java`, `dto/response/ChatResponse.java`, `service/AiChatService.java`, `controller/AiChatController.java`.

**Modified:** `pom.xml` (`spring-ai-bom` 2.0.0, `spring-ai-starter-model-openai`, validation, `spring-boot-webmvc-test`), `application.yml` (`spring.ai.openai.*`), `GlobalExceptionHandler` (validation handler).

**Issue & fix (one cycle, live-only):**
- **Problem:** first live chat call → `502 AI_SERVICE_ERROR`. The actual root cause was **invisible** because `AiChatService` logged `log.error("AI model invocation failed")` **without passing the exception** — a genuine logging defect caught and fixed as part of diagnosing this (`log.error("AI model invocation failed", ex)`), not just a one-off fix.
- **Real cause (revealed once logging was fixed... but actually the user pre-empted the diagnosis):** the default model id `gpt-4o-mini` isn't valid on the Capgemini gateway. **User supplied the correct value directly: `openai.gpt-5-mini`.**
- **Fix:** changed the default in `application.yml` to `openai.gpt-5-mini` (still overridable via `OPENAI_CHAT_MODEL`), plus the logging fix above (kept permanently, not reverted).

**Validation result:** `mvn clean verify` → 10/10 tests (mocked, no API key needed). Live: real, coherent OpenAI-generated response through the corporate gateway; blank message → 400 before any API call.

---

## STEP-10 — Prompt Templates and Structured Output

**Objective:** Externalize the system prompt into a `PromptTemplate` resource file; add structured-requirement extraction (`POST /api/ai/requirements` → `PropertyRequirementResponse { city, bhk, maxBudget, parkingRequired }`).

**Created:** `prompts/property-assistant.st` (template with `{userMessage}`/`{conversationHistory}` placeholders — **deliberately omitting** `{propertyContext}`/`{ragContext}` at this point, since those aren't wired up until STEP-13, avoiding speculative unused placeholders), `prompt/PromptConfig.java` (loads the `.st` resource, exposes a `PromptTemplate` bean), `dto/response/PropertyRequirementResponse.java`.

**Modified:** `AiConfig` (removed the old inline system-prompt string), `AiChatService` (renders the template per-request; added `extractRequirement()` using Spring AI's built-in `.entity(Class)` structured-output mechanism, backed by `BeanOutputConverter`), `AiChatController` (new endpoint).

**Issues:** None — built and validated clean on the first attempt (unusual for this project's AI-integration STEPs).

**Validation result:** `mvn clean verify` → 14/14 tests. Live: `/api/ai/requirements` correctly extracted `{"city":"Pune","bhk":2,"maxBudget":8000000,"parkingRequired":true}` from natural language; `/api/ai/chat` still worked correctly through the new externalized template.

---

## STEP-11 — Persistent Chat History and Memory

**Objective:** `Conversation`/`ChatMessage` entities, real PostgreSQL persistence of every turn, `ChatMemoryService` populating the (previously placeholder) `conversationHistory` prompt variable, `GET /api/ai/conversations/{id}`.

**Auth-boundary decision (same pattern as STEP-06):** `ChatRequest` gained a required `userId` field (no JWT context available in ai-service either) — a conversation's `conversationId` is validated to belong to the requesting `userId` (403 on mismatch), matching `02-validation-contract.md §6` exactly.

**Scope decision (explicitly stated, not silent):** no explicit `POST /api/ai/conversations` endpoint — `/api/ai/chat` auto-creates a conversation when `conversationId` is omitted (title auto-derived from the first message, truncated to 50 chars), matching the documented example flow. Only retrieval (`GET .../{id}`) was added as a separate endpoint since nothing else needed it explicitly.

**Design decision:** manual persistence via our own `Conversation`/`ChatMessage` JPA entities and repositories, **not** Spring AI's built-in `ChatMemory`/`MessageWindowChatMemory` abstraction — avoids a second, parallel persistence mechanism alongside entities the project needs anyway (for `title`/timestamps in a future admin view).

**Created:** `entity/{Conversation,ChatMessage,MessageRole}.java`, `repository/{ConversationRepository,ChatMessageRepository}.java`, `memory/ChatMemoryService.java`, `dto/response/{ConversationResponse,ChatMessageResponse}.java`, `controller/ConversationController.java`, `exception/{ConversationNotFoundException,ForbiddenException}.java`.

**Modified:** `pom.xml` (data-jpa, postgresql driver — first time ai-service needed its own DB), `application.yml` (new `propertyhub_ai` database), `ChatRequest` (+userId), `AiChatService` (full `chat()` rewrite: resolve/create conversation, load history, persist both turns), `GlobalExceptionHandler`.

**Issue & fix (one cycle — the recurring Mockito field-initializer bug from STEP-05):**
- Same root cause as STEP-05 item #2: `ChatMemoryServiceTest` constructed the service-under-test as a field initializer depending on `@Mock` fields. **Fix:** moved to `@BeforeEach`, same as before.

**Validation result:** `mvn clean verify` → 21/21 tests. Live: two-turn conversation — turn 2 ("What budget did I just mention?") correctly recalled ₹80 lakh from turn 1 via the now-populated `conversationHistory`; `GET /api/ai/conversations/{id}` returned all 4 messages in order; **and**, after a full ai-service restart, the same conversation was still retrievable — proving genuine PostgreSQL persistence, not in-memory state.

---

## STEP-12 — Embeddings and pgvector

**Objective:** `EmbeddingModel` + `PgVectorStore`, ingest 6 named knowledge documents, prove similarity search works (standalone, **not yet wired into the chat flow** — that's STEP-13).

**Pre-check performed:** confirmed pgvector 0.8.3 was already installed (not just theoretically available) on the local PostgreSQL 17 before planning, avoiding a potential hard blocker.

**Created:** `knowledge/{pune-locality-guide,hinjewadi-guide,wakad-guide,property-buying-faq,home-loan-faq,property-documentation-guide}.md` (genuine, substantive content — not filler, since STEP-13's RAG demo depends on real semantic content existing), `rag/{DocumentIngestionService,VectorSearchService}.java`, `dto/response/{IngestionResponse,KnowledgeSearchResult}.java`, `controller/KnowledgeController.java` (`POST /api/ai/knowledge/ingest`, `GET /api/ai/knowledge/search`).

**Modified:** `pom.xml` (`spring-ai-starter-vector-store-pgvector`), `application.yml` (`spring.ai.vectorstore.pgvector.*`: `initialize-schema: true` — required explicit opt-in in Spring AI 2.0, a breaking change from earlier versions; `HNSW` index, `COSINE_DISTANCE`).

**Idempotency design:** `DocumentIngestionService` checks `SELECT COUNT(*) FROM vector_store` via `JdbcTemplate` before ingesting — skips (returns `0`) if already populated, avoiding duplicate entries on repeated calls.

**Issue & fix (one cycle, live-only — the embedding-model-naming asymmetry noted in §1):**
- **Problem:** ingestion → `502`, root cause (once logs were inspected) `com.openai.errors.BadRequestException: 400` from the embedding API. The `openai.text-embedding-3-small` model id (guessed by analogy to the working chat-model prefix pattern) was wrong.
- **Fix (user-supplied):** drop the `openai.` prefix for the embedding model AND add a `/v1` suffix to the embedding-specific base URL (`spring.ai.openai.embedding.base-url`, distinct from the chat base-url) — this is a real, permanent asymmetry in how this specific corporate gateway routes chat vs. embedding calls, not a mistake to "fix" later.

**Validation result:** `mvn clean verify` → 26/26 tests. Live: all 6 documents ingested; similarity search correctly ranked the Hinjewadi guide top (score 0.74) for an IT-professional query and the Property Documentation Guide top (0.66) for a buying-documents query; re-running ingestion correctly returned `0` (idempotency confirmed).

---

## STEP-13 — RAG (the primary AI demonstration)

**Objective:** Wire everything built so far (STEP-08 Feign, STEP-10 structured extraction, STEP-11 persistence, STEP-12 pgvector) into the full documented flow: `Natural Language → Structured Requirement → Property Search via Feign → Candidate Properties → Locality RAG → AI Recommendation`. This is explicitly "the main AI demonstration" and the mandatory MVP advanced-AI completion milestone.

**Design decisions (all stated explicitly in the plan, not silent):**
- Extends the **existing** `POST /api/ai/chat` (unchanged request/response shape) rather than a new parallel endpoint — matches the documented single-flow architecture.
- **Property search only triggers when structured extraction yields at least one concrete criterion** (city, bhk, or maxBudget) — not on every message, avoiding wasteful calls to property-service for small talk.
- **Knowledge-base RAG search runs on every message** (cheap vector search, no extra LLM call) but with a `similarityThreshold` (0.5) so irrelevant queries don't inject noise.
- **Best-effort context enrichment:** if structured extraction, property search, or RAG search each fail independently, the flow degrades gracefully (placeholder text like `"(property search unavailable)"`) rather than failing the whole chat call — only a failure of the *final* grounded-answer model call actually errors out.
- The prompt template finally got its last two documented placeholders (`propertyContext`, `ragContext`).

**Modified:** `prompts/property-assistant.st` (added the two sections), `AiChatService` (`chat()` now orchestrates extraction → conditional property search → RAG search → full prompt → final answer; extraction logic refactored into a shared private method reused by both `/chat` and `/requirements`), `VectorSearchService` (new `searchForContext(query)` — topK=3 + threshold=0.5 — separate from the existing unrestricted diagnostic `search(query, topK)` used by `KnowledgeController`).

**No new files, no database changes** — this STEP is purely orchestration of existing capabilities.

**Issues:** None — built and validated clean on the first attempt.

**Validation result:** `mvn clean verify` → 33/33 tests. **Live showcase query** — *"Find me a 2 BHK in Pune under 80 lakh and tell me if the locality is suitable for an IT professional"* — produced a fully grounded answer citing the **exact real property** from property-service (₹72 lakh, Hinjewadi, 1150 sqft, parking, via Feign) and **precise real locality facts** from the ingested Hinjewadi guide (Rajiv Gandhi Infotech Park, short IT commute, the Hinjewadi-Wakad traffic caveat, expanding metro connectivity) rather than generic AI knowledge — confirming the full pipeline works end-to-end. This completes "MVP = everything through RAG" per the development plan.

---

## STEP-14 — Cross-Cutting Engineering Hardening

**Objective (per `01-development-plan.md`, full 8-item list — see the "missed scope" note in §2 above):** meaningful logging, global exceptions, Actuator, Swagger/OpenAPI, Spring Boot Admin registration, security restrictions for management endpoints, **switch to Lombok**, **ModelMapper Bean Approach**.

**Scope per item:**
1. **Logging** — already adequate from STEPs 04-13; folded into the Lombok `@Slf4j` change (item 7).
2. **Global exceptions** — already present in auth/property/ai-service; api-gateway intentionally not given one (pure proxy, no business exceptions).
3. **Actuator** — added `metrics` to `management.endpoints.web.exposure.include` (was `health,info`) across **all 6 services**.
4. **Swagger/OpenAPI** — added `springdoc-openapi-starter-webmvc-ui:3.0.3` to auth/property/ai-service only (the three with real REST APIs, matching requirements §45's "at minimum"). auth-service's `SecurityConfig` needed `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html` explicitly permitted, or Swagger UI 401s against its own JWT filter.
5. **Spring Boot Admin registration** — verified **live**, no code changes needed (STEP-01's Eureka-discovery design was already correct).
6. **Security restrictions for management endpoints** — scope boundary explicitly stated: only auth-service has security wired (the STEP-06/08 gap), so the restriction applied everywhere was a minimal actuator **whitelist** (`health,info,metrics`, not `*`) rather than per-service auth gating, which would require retrofitting security into 3 services — treated as part of the still-open security gap, not solved piecemeal here.
7. **Switch to Lombok** — entities (`User`, `Property`, `Conversation`, `ChatMessage`) got `@Getter` + `@NoArgsConstructor(access = PROTECTED)`, **keeping the custom business constructors hand-written** (Lombok's `@AllArgsConstructor` would generate a constructor over *every* field including `id`/`createdAt`, which doesn't match the intentional "constructor for creation" pattern). All 7 files with manual `Logger log = LoggerFactory.getLogger(...)` fields switched to `@Slf4j`: `AiChatService`, `VectorSearchService`, `DocumentIngestionService`, `PropertyClientService`, `PropertyService`, `AuthService`, `RequestLoggingFilter` (api-gateway).
8. **ModelMapper Bean Approach** — scoped **deliberately to the entity→response-DTO direction only** (the read side); the request-DTO→entity direction stayed explicit (business logic like password encoding lives there and shouldn't be silently automated away). `PropertyMapper` became a `@Component` wrapping an injected `ModelMapper` for `toResponse`/`toSummary` (kept `toEntity` manual). `AuthService`'s inline `UserResponse` construction became `modelMapper.map(user, UserResponse.class)`. Each of the 3 services got a small `ModelMapperConfig` (`@Bean ModelMapper` with `RecordModule` registered, needed since all DTOs are Java records).

**Issues & fixes (three cycles, all around the ModelMapper + records integration — the other 7 items built clean):**
1. **Wrong package name for the record module.** Web search results (and even the artifact's own advertised usage) said `org.modelmapper.module.record.RecordModule` — this package **does not exist**. Inspecting the actual jar (`unzip -l`) showed the real package is `org.modelmapper.record.RecordModule`. **Fixed across all 6 files** (3 main `ModelMapperConfig`s + 3 test files) referencing it.
2. **Binary incompatibility between `modelmapper-module-record:1.0.1` and `modelmapper:3.2.4`** — `NoClassDefFoundError: org/modelmapper/spi/ConstructorInjector` at test-class construction time. Diagnosed by inspecting the record-module jar's own `pom.xml` inside `META-INF/maven/...` — it was built against **`modelmapper 3.2.6`**, not 3.2.4. **Fixed** by bumping the core `modelmapper` dependency to `3.2.6` in all 3 services. **Lesson: when adding a "module"/"plugin" artifact for a library, always check what core version it was actually built against — don't assume the latest core version is compatible with the latest module version.**
3. **Record-module can't bridge an enum-to-String type mismatch during canonical-constructor matching.** `ChatMessage.role` is a `MessageRole` enum but `ChatMessageResponse.role` is a `String` (deliberately, in the DTO) — `modelMapper.map(chatMessage, ChatMessageResponse.class)` threw `MappingException: Failed to instantiate instance of destination ... Ensure that ... has a non-private no-argument constructor` (a misleading error message — the real issue was the type mismatch preventing record-constructor resolution, not a missing no-arg constructor). All *other* entity→record mappings in the project have exact type matches (e.g. `Role`↔`Role`, `PropertyType`↔`PropertyType`) and worked fine with ModelMapper — this was the one deliberately-mismatched field. **Fix:** reverted just this one mapping (`ConversationController.toMessageResponse`) to manual construction (4-line record constructor call), removed the now-unused `ModelMapper` dependency from `ConversationController` entirely. This was **anticipated and pre-flagged in the STEP-14 plan** before building, and confirmed live exactly as predicted.

**Validation result:** all 6 `mvn clean verify` runs pass with **identical or expected test counts to before the STEP** (proving the refactor is behavior-preserving — this was the actual test strategy for this STEP, since no new business logic was added). Live: `/actuator/metrics` returns 200 on all 6 services; Swagger UI + `/v3/api-docs` live on auth/property/ai-service; Spring Boot Admin's `/instances` (fetched with `Accept: application/json`, since the bare path serves an HTML SPA shell) correctly listed all 5 running services (auth, ai, property, admin-server, api-gateway) as `"status":"UP"` via pure Eureka auto-discovery.

---

## STEP-15 — Shared Validation Contract Completion

**Objective (per `01-development-plan.md`):** verify and align validation semantics across Frontend/Backend/PostgreSQL; backend is authoritative, database enforces persistence invariants, frontend provides early feedback (frontend not yet built — STEP-16+, so this STEP audited backend+database only). Create/verify shared validation error semantics.

**Audit findings (via live inspection, not from the plan text itself, which is only a short paragraph):**
1. Invalid enum values in request bodies (e.g. `role: "NOPE"`, `propertyType: "NOPE"`) returned Spring Boot's raw default error body instead of the project's `ErrorResponse` shape, because `HttpMessageNotReadableException` (thrown during JSON deserialization, before Bean Validation even runs) wasn't handled in auth-service's or property-service's `GlobalExceptionHandler`.
2. `ai-service`'s exception taxonomy was missing a distinct `VectorSearchException`/`VECTOR_SEARCH_ERROR` category — pgvector/vector-store failures were using the generic `AiServiceException`/`AI_SERVICE_ERROR`, conflating two different failure domains (LLM call failures vs. vector-store failures).

**Created:**
- `ai-service/src/main/java/com/propertyhub/ai/exception/VectorSearchException.java`.

**Modified:**
- `auth-service/.../exception/GlobalExceptionHandler.java` — added `HttpMessageNotReadableException` handler → `400 VALIDATION_ERROR`.
- `property-service/.../exception/GlobalExceptionHandler.java` — identical handler added.
- `ai-service/.../exception/GlobalExceptionHandler.java` — added `VectorSearchException` handler → `502 VECTOR_SEARCH_ERROR`.
- `ai-service/.../rag/VectorSearchService.java` and `.../rag/DocumentIngestionService.java` — both throw sites switched from `AiServiceException` to `VectorSearchException`.
- `auth-service/.../controller/AuthControllerTest.java` — added `returns400OnInvalidRoleEnumValue`.
- `property-service/.../controller/PropertyControllerTest.java` — added `returns400OnInvalidPropertyTypeEnumValue`.
- `ai-service/.../rag/VectorSearchServiceTest.java` — added `searchThrowsVectorSearchExceptionWhenStoreFails`, `searchForContextThrowsVectorSearchExceptionWhenStoreFails`.
- `ai-service/.../rag/DocumentIngestionServiceTest.java` — added `throwsVectorSearchExceptionWhenStoreFails`.

**Key decision:** the approved plan's "Testing" section assumed existing failure-path assertions would be *updated*; live inspection showed no such assertions existed yet for vector-store failures, so these are net-new tests rather than updates — noted explicitly rather than silently treated as a plan mismatch.

**Issue & fix (environment, not code — hit during validation, not build):**
- **Problem:** `mvn clean verify` failed identically across all three services with `cannot find symbol: log` / missing Lombok-generated getters (`JwtService`, `PropertyService`, `ChatMemoryService`, `Conversation`, `ChatMessage`, etc. — files this STEP never touched). Root cause: Maven was invoked with the machine's default JDK 25 on `PATH` instead of the project's fixed-target JDK 21, so Lombok's annotation processor never ran. This is a **pre-existing, already-documented environment requirement** (see §1 above), not a new defect — the fix was simply re-running with `JAVA_HOME` pointed at `D:\java\jdk-21.0.11` before invoking `mvn`, per the established pattern.

**Validation result:** `mvn clean verify` (under JDK 21) → auth-service 22/22, property-service 29/29, ai-service 36/36, all BUILD SUCCESS. Live: `POST /api/auth/register` with `role: "NOPE"` and `POST /api/properties` with `propertyType: "NOPE"` both returned `400` with the proper `{"error":"VALIDATION_ERROR", ...}` body (confirmed via curl against live-started auth-service and property-service instances, then cleanly stopped).

---

# 4. Cumulative Dependency/Version Reference (as of end of STEP-14)

```text
Spring Boot                4.1.0
Spring Cloud (BOM)          2025.1.2
Spring AI (BOM)             2.0.0
springdoc-openapi            3.0.3  (webmvc-ui starter)
jjwt (api/impl/jackson)      0.13.0
PostgreSQL JDBC driver       42.7.9
pgvector extension           0.8.3 (PostgreSQL-side)
modelmapper                  3.2.6  (NOT 3.2.4 — binary-incompatible with modelmapper-module-record 1.0.1)
modelmapper-module-record    1.0.1  (package is org.modelmapper.record, NOT org.modelmapper.module.record)
Lombok                       (version managed by Spring Boot parent BOM)
feign-okhttp                 (version managed by Spring Cloud BOM)
```

---

# 5. Known Open Items / Gaps (not yet resolved, intentionally flagged)

1. **Security gap:** `property-service`, `ai-service`, `api-gateway` have no authentication on their business endpoints. Only `auth-service` has JWT/Spring Security. This needs a dedicated future STEP — do not patch piecemeal.
2. **Actuator endpoints are fully open (whitelist of `health,info,metrics`, not authenticated)** on all services except that auth-service's JWT filter explicitly permits them — this is linked to item 1 above; proper actuator security requires the broader security rollout first.
3. Property Service's `agentId` and AI Service's `userId` are both supplied directly in request bodies (no JWT-derived identity) — will need to change once security is added everywhere.

---

# 6. Next STEP

Per `01-development-plan.md`: **STEP-16 — Buyer/Agent React Application Foundation** (first frontend STEP — Axios client, route/screen structure, Claymorphism design system, form/card/table components, loading/empty/error states, toast system, authentication state). Not started as of this document's writing. Do not begin it automatically — wait for explicit user instruction, per `CLAUDE.md`'s lifecycle rules.
