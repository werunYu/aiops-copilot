package site.werun.aiops.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A single workflow or tool event associated with an incident.
 */
@Table("agent_event")
public record AgentEvent(
        @Id Long id,
        @Column("incident_id") Long incidentId,
        @Column("event_type") String eventType,
        @Column("tool_name") String toolName,
        String content,
        String status,
        @Column("created_at") LocalDateTime createdAt) {
}
