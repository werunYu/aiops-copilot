package site.werun.aiops.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * An alert incident tracked by the AIOps workflow.
 */
@Table("incident")
public record Incident(
        @Id Long id,
        @Column("service_name") String serviceName,
        String environment,
        String title,
        @Column("raw_alert") String rawAlert,
        String status,
        @Column("created_at") LocalDateTime createdAt,
        @Column("updated_at") LocalDateTime updatedAt) {
}
