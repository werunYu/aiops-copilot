package site.werun.aiops.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/** Provides CRUD access to root-cause analysis reports. */
public interface RcaReportRepository extends CrudRepository<RcaReport, Long> {

    Optional<RcaReport> findTopByIncidentIdOrderByCreatedAtDesc(Long incidentId);
}
