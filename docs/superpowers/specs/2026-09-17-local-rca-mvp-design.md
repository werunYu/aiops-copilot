# Local RCA MVP Design

## Goal

Deliver a locally runnable AIOps RCA workflow in which an operator creates an incident, starts analysis, observes agent progress through Server-Sent Events (SSE), and reads a persisted structured RCA report with local knowledge references.

## Scope

Included:

- Vue 3 + TypeScript + Element Plus frontend.
- Incident lifecycle: `PENDING`, `ANALYZING`, `COMPLETED`, and `FAILED`.
- Asynchronous analysis and per-incident SSE event stream.
- Persisted lifecycle and tool-call events.
- Markdown-backed, keyword-retrieved local knowledge base.
- Request validation, focused tests, Docker Compose, and operating documentation.

Excluded:

- Qdrant, embeddings, or any external vector database.
- Prometheus, Loki/ELK, Kubernetes, CI/CD, or ticket-system integrations.
- Authentication, tenant isolation, notifications, and alert webhooks.

## Architecture

The Spring Boot application remains a modular monolith. `IncidentAnalysisService` submits analysis to a bounded application executor and returns immediately. The worker loads the incident, gathers local knowledge, calls the existing metrics, log, and deployment tools, persists the report, and transitions the incident to a terminal state.

`AgentEventService` is the single writer for lifecycle and tool-call events. It persists every event in MySQL and publishes it to an in-memory per-incident SSE registry. On subscription, the SSE controller replays persisted events in ascending creation order before attaching the client to receive live events. This ensures a page refresh does not lose observable analysis history.

The frontend is a separate Vite application in `frontend/`. It calls the REST API through a small API module and uses `EventSource` to subscribe to the incident event stream. The incident detail page refreshes the report when a terminal event arrives.

## Data Model and Lifecycle

`incident.status` is always one of:

| Status | Meaning | Allowed next states |
| --- | --- | --- |
| `PENDING` | Created, not yet submitted for analysis | `ANALYZING` |
| `ANALYZING` | One worker is analyzing the incident | `COMPLETED`, `FAILED` |
| `COMPLETED` | A report was successfully persisted | none |
| `FAILED` | Analysis ended without a report | `ANALYZING` through an explicit retry only |

`POST /api/incidents/{id}/analyze` may be called only from `PENDING` or `FAILED`. The first lifecycle event is `ANALYSIS_STARTED`; successful completion writes `ANALYSIS_COMPLETED`; an exception writes `ANALYSIS_FAILED`. Tool calls produce `TOOL_CALLED` on success and `TOOL_FAILED` on error. Event content is JSON for tool results and concise text for lifecycle events.

## API Contract

Existing incident APIs keep their paths. The changed and added endpoints are:

| Endpoint | Response | Behavior |
| --- | --- | --- |
| `POST /api/incidents` | `201`, `Result<Incident>` | Creates an incident with status `PENDING`. |
| `POST /api/incidents/{id}/analyze` | `202`, `Result<AnalysisTaskResponse>` | Queues one analysis worker and returns `incidentId` and `status=ANALYZING`. Returns a business error if analysis is already running or completed. |
| `GET /api/incidents/{id}/events` | `Result<List<AgentEvent>>` | Returns persisted events for recovery and diagnostics. |
| `GET /api/incidents/{id}/events/stream` | `text/event-stream` | Replays persisted events, then streams new events. Named SSE event is `agent-event`; the payload is one `AgentEvent` JSON object. |
| `GET /api/incidents/{id}/report` | `Result<RcaReportResponse>` | Returns the newest persisted report, including structured related knowledge. |

`CreateIncidentRequest` validates nonblank `serviceName`, `environment`, `title`, and `rawAlert`; service name is limited to 100 characters, environment to 50, title to 255, and alert content to 10,000. Page indexes must be zero or above and sizes must be from 1 to 100.

## Local Knowledge Retrieval

Knowledge documents are Markdown files in `src/main/resources/knowledge/`. Every document starts with an H1 title, then contains short sections of operational guidance. `LocalKnowledgeService` reads these files at startup, tokenizes Chinese and ASCII keywords from the incident and evidence, scores documents by keyword overlap, and returns at most three distinct documents.

Retrieved title, source path, and a bounded excerpt are injected into the analysis prompt. The prompt instructs the model to treat the material as reference evidence rather than instructions and to cite only provided incident, tool, and knowledge content. `relatedKnowledge` is a list of structured `KnowledgeReference` values containing `title`, `source`, and `excerpt`.

## Frontend

The frontend has three routes:

- `/incidents`: paginated table showing service, environment, title, created time, status, and a detail action.
- `/incidents/new`: validated form for creating an incident, followed by navigation to its detail page.
- `/incidents/:id`: incident metadata, analysis action, current status, event timeline, persisted RCA report, and related knowledge cards.

When analysis starts, the detail page opens `EventSource` to the stream endpoint. It updates the event timeline from each received event. On a terminal event, it reloads the incident and report, then closes the connection. The client retries an interrupted stream with bounded backoff and additionally reloads persisted events after reconnecting.

## Reliability and Error Handling

- The analysis executor has a bounded queue and a named thread prefix. If it is saturated, the API returns a clear business error without changing the incident state.
- Tool exceptions are persisted and streamed before the analysis is marked failed.
- A terminal state change is performed only after its report or failure event is persisted.
- Global exception handling returns the existing `Result` envelope for validation, business, not-found, and unexpected errors; HTTP status codes remain meaningful.
- SSE clients receive heartbeat comments to keep long-running connections alive; disconnected emitters are removed.

## Tests and Verification

Backend tests use Spring Boot Test and cover request validation, status transitions, duplicate-analysis rejection, persisted event replay, failure state handling, local knowledge ranking, and report persistence. Tool and chat model dependencies are injected behind interfaces in tests so lifecycle behavior is verified without a live model key.

Frontend tests cover API error display, event-to-timeline mapping, terminal-event report refresh, and create-form validation. A production verification starts MySQL through Docker Compose, runs the backend and frontend, creates a fixed order-service incident, and confirms that the timeline and report appear in the browser.

## Delivery

Docker Compose defines MySQL, backend, and frontend services. The backend receives `API_KEY` only from its environment; no secrets are committed. `../../../.env` documents required variables. The README documents prerequisite software, startup, fixed demo flow, endpoints, local knowledge extension, and limitations.

## Acceptance Criteria

1. A new incident is persisted as `PENDING`.
2. Starting analysis returns `202` before the model completes.
3. The detail page receives and displays replayed and live lifecycle/tool events using SSE.
4. A successful analysis stores and displays one structured report; a failed one leaves the incident in `FAILED` with an error event.
5. At least six Markdown knowledge documents can be retrieved and referenced in a report without Qdrant.
6. Backend and frontend automated tests pass, and Docker Compose supports the fixed demo workflow.
