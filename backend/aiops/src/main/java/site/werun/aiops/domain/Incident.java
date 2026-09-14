package site.werun.aiops.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
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

        @Column("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @Column("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt) {

    public static Incident of(String serviceName, String environment, String title, String rawAlert, String status) {
        return new Incident(null, serviceName, environment, title, rawAlert, status, LocalDateTime.now(), LocalDateTime.now());
    }
}
