CREATE TABLE incident
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(100) NOT NULL,
    environment  VARCHAR(50),
    title        VARCHAR(255),
    raw_alert    TEXT,
    status       VARCHAR(30),
    created_at   DATETIME,
    updated_at   DATETIME
);

CREATE TABLE rca_report
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    incident_id BIGINT NOT NULL,
    report_json JSON,
    model_name  VARCHAR(100),
    duration_ms BIGINT,
    created_at  DATETIME
);

CREATE TABLE agent_event
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    incident_id BIGINT NOT NULL,
    event_type  VARCHAR(50),
    tool_name   VARCHAR(100),
    content     TEXT,
    status      VARCHAR(30),
    created_at  DATETIME
);