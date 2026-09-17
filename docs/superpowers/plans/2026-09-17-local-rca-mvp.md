# Local RCA MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (- [ ]) syntax for tracking.

**Goal:** Build the confirmed local RCA MVP: asynchronous analysis, persisted SSE progress, Markdown knowledge retrieval, Vue 3 UI, and reproducible startup.

**Architecture:** MySQL is the source of truth. A bounded Spring executor performs analysis; an in-memory emitter registry delivers events after persistence. Local Markdown knowledge is retrieved by keyword overlap. Vue 3 is a separate Vite project.

**Tech Stack:** Java 21, Spring Boot, Spring Data JDBC, Spring AI, MySQL, JUnit 5, Vue 3, TypeScript, Vite, Element Plus, Docker Compose.

**Spec:** docs/superpowers/specs/2026-09-17-local-rca-mvp-design.md

## Global Constraints

- Keep the existing Result envelope for JSON endpoints.
- Read API_KEY from environment; never commit it.
- Do not add Qdrant, embedding models, external observability platforms, authentication, or notifications.
- Persist an event before publishing it over SSE.
- Permit only PENDING to ANALYZING to COMPLETED or FAILED state transitions.

---

### Task 1: Incident lifecycle and request validation

**Files:**
- Modify: backend/aiops/src/main/java/site/werun/aiops/enums/IncidentStatusEnum.java
- Modify: backend/aiops/src/main/java/site/werun/aiops/service/IncidentService.java
- Modify: backend/aiops/src/main/java/site/werun/aiops/request/CreateIncidentRequest.java
- Modify: backend/aiops/src/main/java/site/werun/aiops/web/IncidentController.java
- Modify: backend/aiops/src/main/java/site/werun/aiops/handler/GlobalExceptionHandler.java
- Create: backend/aiops/src/main/java/site/werun/aiops/response/AnalysisTaskResponse.java
- Create: backend/aiops/src/test/java/site/werun/aiops/service/IncidentServiceTest.java

**Interfaces:**
- Consumes: CreateIncidentRequest(serviceName, environment, title, rawAlert).
- Produces: a PENDING Incident and AnalysisTaskResponse(Long incidentId, String status).

- [ ] **Step 1: Write the failing test**

    @Test
    void save_assigns_pending_status() {
        Incident result = service.save(request("order-service"));
        assertThat(result.status()).isEqualTo("PENDING");
    }

    @Test
    void requireAnalyzable_rejects_completed_incident() {
        assertThatThrownBy(() -> service.requireAnalyzable(completed))
            .isInstanceOf(ServiceException.class);
    }

- [ ] **Step 2: Verify RED**

    Run: mvn -Dtest=IncidentServiceTest test

    Expected: FAIL because PENDING and requireAnalyzable do not exist.

- [ ] **Step 3: Implement the minimal behavior**

    public enum IncidentStatusEnum {
        PENDING("PENDING", "待分析"), ANALYZING(...), COMPLETED(...), FAILED(...)
    }

    public Incident save(CreateIncidentRequest request) {
        return incidentRepository.save(Incident.of(..., IncidentStatusEnum.PENDING.getStatus()));
    }

    Add NotBlank and Size validation: serviceName 100, environment 50, title 255, rawAlert 10000. Add Min(0) and Max(100) pagination validation. Map validation and business exceptions to Result.

- [ ] **Step 4: Verify GREEN**

    Run: mvn -Dtest=IncidentServiceTest test

    Expected: PASS.

- [ ] **Step 5: Commit**

    git add backend/aiops/src/main backend/aiops/src/test
    git commit -m "feat: validate incident lifecycle"

### Task 2: Persisted SSE event stream

**Files:**
- Modify: backend/aiops/src/main/java/site/werun/aiops/service/AgentEventService.java
- Create: backend/aiops/src/main/java/site/werun/aiops/service/AgentEventPublisher.java
- Create: backend/aiops/src/main/java/site/werun/aiops/service/SseEmitterRegistry.java
- Create: backend/aiops/src/main/java/site/werun/aiops/web/IncidentEventController.java
- Create: backend/aiops/src/test/java/site/werun/aiops/service/AgentEventServiceTest.java
- Create: backend/aiops/src/test/java/site/werun/aiops/web/IncidentEventControllerTest.java

**Interfaces:**
- Consumes: AgentEventService.save(incidentId, eventType, toolName, content, status).
- Produces: GET /api/incidents/{id}/events/stream, event name agent-event, AgentEvent JSON data.

- [ ] **Step 1: Write the failing tests**

    @Test
    void save_persists_before_publishing() {
        AgentEvent result = service.save(42L, "ANALYSIS_STARTED", null, "started", "SUCCESS");
        then(repository).should().save(any(AgentEvent.class));
        then(publisher).should().publish(result);
    }

    @Test
    void stream_replays_events_before_subscribing() throws Exception {
        mockMvc.perform(get("/api/incidents/42/events/stream"))
            .andExpect(request().asyncStarted());
    }

- [ ] **Step 2: Verify RED**

    Run: mvn -Dtest=AgentEventServiceTest,IncidentEventControllerTest test

    Expected: FAIL because no publisher, registry, or stream endpoint exists.

- [ ] **Step 3: Implement persistence, replay, and publication**

    public interface AgentEventPublisher {
        void publish(AgentEvent event);
    }

    public SseEmitter subscribe(Long incidentId) {
        SseEmitter emitter = new SseEmitter(0L);
        eventService.findEventByIncidentId(incidentId).forEach(event -> send(emitter, event));
        emitters.computeIfAbsent(incidentId, ignored -> new CopyOnWriteArraySet<>()).add(emitter);
        return emitter;
    }

    Publish only after repository save succeeds. Name every SSE event agent-event. Remove completed, timeout, and error emitters.

- [ ] **Step 4: Verify GREEN**

    Run: mvn -Dtest=AgentEventServiceTest,IncidentEventControllerTest test

    Expected: PASS.

- [ ] **Step 5: Commit**

    git add backend/aiops/src/main backend/aiops/src/test
    git commit -m "feat: stream persisted agent events"

### Task 3: Asynchronous, observable analysis

**Files:**
- Modify: backend/aiops/src/main/java/site/werun/aiops/service/IncidentAnalysisService.java
- Modify: backend/aiops/src/main/java/site/werun/aiops/web/IncidentController.java
- Modify: backend/aiops/src/main/java/site/werun/aiops/tools/MetricsTool.java
- Modify: backend/aiops/src/main/java/site/werun/aiops/tools/LogQueryTool.java
- Modify: backend/aiops/src/main/java/site/werun/aiops/tools/DeploymentTool.java
- Create: backend/aiops/src/main/java/site/werun/aiops/configuration/AnalysisExecutorConfiguration.java
- Create: backend/aiops/src/test/java/site/werun/aiops/service/IncidentAnalysisServiceTest.java

**Interfaces:**
- Consumes: IncidentAnalysisService.start(Long incidentId).
- Produces: immediate AnalysisTaskResponse and ANALYSIS_STARTED, ANALYSIS_COMPLETED, or ANALYSIS_FAILED events.

- [ ] **Step 1: Write the failing tests**

    @Test
    void start_marks_incident_analyzing_and_queues_worker() {
        AnalysisTaskResponse task = service.start(incident.id());
        assertThat(task.status()).isEqualTo("ANALYZING");
        then(executor).should().execute(any(Runnable.class));
    }

    @Test
    void worker_writes_failure_event_when_model_throws() {
        worker.run();
        then(incidentService).should().updateStatus(incident.id(), "FAILED");
        then(events).should().save(incident.id(), "ANALYSIS_FAILED", null, contains("model"), "FAILED");
    }

- [ ] **Step 2: Verify RED**

    Run: mvn -Dtest=IncidentAnalysisServiceTest test

    Expected: FAIL because analysis is synchronous and no executor is injected.

- [ ] **Step 3: Implement queueing and worker execution**

    public AnalysisTaskResponse start(Long incidentId) {
        incidentService.requireAnalyzable(incidentService.findById(incidentId));
        incidentService.updateStatus(incidentId, ANALYZING.getStatus());
        eventService.save(incidentId, "ANALYSIS_STARTED", null, "analysis started", "SUCCESS");
        executor.execute(() -> analyzeInWorker(incidentId));
        return new AnalysisTaskResponse(incidentId, ANALYZING.getStatus());
    }

    Add a bounded ThreadPoolTaskExecutor called analysisExecutor. Save the report before COMPLETED. Write a concise failure event before FAILED. Each tool writes TOOL_FAILED before rethrowing.

- [ ] **Step 4: Verify GREEN**

    Run: mvn -Dtest=IncidentAnalysisServiceTest test

    Expected: PASS.

- [ ] **Step 5: Commit**

    git add backend/aiops/src/main backend/aiops/src/test
    git commit -m "feat: run incident analysis asynchronously"

### Task 4: Local Markdown knowledge retrieval

**Files:**
- Create: backend/aiops/src/main/java/site/werun/aiops/knowledge/KnowledgeDocument.java
- Create: backend/aiops/src/main/java/site/werun/aiops/knowledge/KnowledgeReference.java
- Create: backend/aiops/src/main/java/site/werun/aiops/knowledge/LocalKnowledgeService.java
- Modify: backend/aiops/src/main/java/site/werun/aiops/dto/RcaAnalyzeReport.java
- Modify: backend/aiops/src/main/java/site/werun/aiops/service/IncidentAnalysisService.java
- Create: six Markdown documents under backend/aiops/src/main/resources/knowledge
- Create: backend/aiops/src/test/java/site/werun/aiops/knowledge/LocalKnowledgeServiceTest.java

**Interfaces:**
- Consumes: LocalKnowledgeService.search(String query, int limit).
- Produces: List of KnowledgeReference(title, source, excerpt), used by RcaAnalyzeReport.relatedKnowledge.

- [ ] **Step 1: Write the failing tests**

    @Test
    void search_returns_connection_pool_document_for_hikari_timeout_query() {
        List<KnowledgeReference> matches = service.search("HikariPool connection timeout", 3);
        assertThat(matches).extracting(KnowledgeReference::title).contains("数据库连接池耗尽");
    }

    @Test
    void search_limits_results() {
        assertThat(service.search("timeout database", 1)).hasSize(1);
    }

- [ ] **Step 2: Verify RED**

    Run: mvn -Dtest=LocalKnowledgeServiceTest test

    Expected: FAIL because the knowledge classes and documents do not exist.

- [ ] **Step 3: Implement deterministic keyword overlap**

    public List<KnowledgeReference> search(String query, int limit) {
        Set<String> terms = tokenize(query);
        return documents.stream()
            .map(document -> scored(document, terms))
            .filter(match -> match.score() > 0)
            .sorted(comparing(ScoredDocument::score).reversed())
            .limit(limit)
            .map(ScoredDocument::reference)
            .toList();
    }

    Load classpath Markdown files, use each H1 as title, retain a 500-character excerpt, inject references into the analysis prompt, and require structured relatedKnowledge in the response schema.

- [ ] **Step 4: Verify GREEN**

    Run: mvn -Dtest=LocalKnowledgeServiceTest test

    Expected: PASS.

- [ ] **Step 5: Commit**

    git add backend/aiops/src/main backend/aiops/src/test
    git commit -m "feat: retrieve local rca knowledge"

### Task 5: Vue incident workflow

**Files:**
- Create: frontend/package.json, vite.config.ts, tsconfig.json, index.html
- Create: frontend/src/main.ts, App.vue, router/index.ts
- Create: frontend/src/api/client.ts, api/incidents.ts, types/incident.ts
- Create: frontend/src/views/IncidentListView.vue, IncidentCreateView.vue, IncidentDetailView.vue
- Create: frontend/src/components/AgentEventTimeline.vue, RcaReportCard.vue, KnowledgeReferenceList.vue
- Create: frontend/src/views/IncidentCreateView.spec.ts and frontend/src/components/AgentEventTimeline.spec.ts

**Interfaces:**
- Consumes: REST and SSE contracts from Tasks 1-4.
- Produces: /incidents, /incidents/new, and /incidents/:id browser routes.

- [ ] **Step 1: Write failing frontend tests**

    it("blocks submission until every incident field is populated", async () => {
      const wrapper = mount(IncidentCreateView);
      await wrapper.find("form").trigger("submit");
      expect(wrapper.text()).toContain("服务名称不能为空");
    });

    it("renders a received SSE event in the timeline", async () => {
      render(AgentEventTimeline, { props: { events: [startedEvent] } });
      expect(screen.getByText("分析开始")).toBeInTheDocument();
    });

- [ ] **Step 2: Verify RED**

    Run: npm run test -- --run

    Expected: FAIL because the frontend project does not exist.

- [ ] **Step 3: Implement the Element Plus application**

    export function openIncidentStream(id, onEvent) {
      const source = new EventSource("/api/incidents/" + id + "/events/stream");
      source.addEventListener("agent-event", event => onEvent(JSON.parse(event.data)));
      return source;
    }

    Implement the API client, validated create form, paginated list, detail page, event timeline, report card, and knowledge cards. When a terminal event arrives, reload incident and report then close the stream. Reconnect an interrupted stream with a bounded retry count.

- [ ] **Step 4: Verify GREEN**

    Run: npm run test -- --run && npm run build

    Expected: PASS and a Vite production build.

- [ ] **Step 5: Commit**

    git add frontend
    git commit -m "feat: add incident analysis frontend"

### Task 6: Containerized demo and documentation

**Files:**
- Create: backend/aiops/Dockerfile, frontend/Dockerfile, docker-compose.yml, .env.example
- Modify: README.md and .gitignore
- Create: backend/aiops/src/test/java/site/werun/aiops/ApiSmokeTest.java

**Interfaces:**
- Consumes: API_KEY supplied by the environment and MySQL service configuration.
- Produces: docker compose up --build startup for database, backend, and frontend.

- [ ] **Step 1: Write a failing smoke test**

    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    class ApiSmokeTest {
        @Test void health_endpoint_is_available() { /* GET /actuator/health expects 200 */ }
    }

- [ ] **Step 2: Verify RED**

    Run: mvn -Dtest=ApiSmokeTest test

    Expected: FAIL until test datasource configuration exists.

- [ ] **Step 3: Add multi-stage images and operating documentation**

    services:
      mysql:
        image: mysql:9.7
      backend:
        build: ./backend/aiops
      frontend:
        build: ./frontend

    Document startup, demo incident creation, SSE observation, knowledge extension, and limitations. Ignore IDE metadata and local environment files.

- [ ] **Step 4: Run full verification**

    Run: mvn test, npm run test -- --run, npm run build, and docker compose config.

    Expected: every test and Compose validation passes.

- [ ] **Step 5: Commit**

    git add README.md .gitignore .env.example docker-compose.yml backend/aiops frontend
    git commit -m "chore: containerize rca mvp demo"

## Plan Review

- Tasks 1-3 cover lifecycle, background analysis, event persistence, and SSE.
- Task 4 covers local Markdown retrieval without Qdrant.
- Task 5 implements all confirmed Vue pages and SSE behavior.
- Task 6 makes the demo reproducible and verifies its artifacts.
- Types AnalysisTaskResponse, AgentEvent, and KnowledgeReference remain consistent across backend and frontend tasks.

