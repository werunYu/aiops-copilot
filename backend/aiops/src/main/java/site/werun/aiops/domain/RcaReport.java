package site.werun.aiops.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The root-cause analysis output produced for an incident.
 */
@Table("rca_report")
public record RcaReport(
        @Id Long id,
        @Column("incident_id") Long incidentId,
        @Column("report_json") String reportJson,
        @Column("model_name") String modelName,
        @Column("duration_ms") Long durationMs,
        @Column("created_at") LocalDateTime createdAt) {
}
