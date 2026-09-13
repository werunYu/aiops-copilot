package site.werun.aiops.domain;

import org.springframework.data.repository.CrudRepository;

/** Provides CRUD access to incidents. */
public interface IncidentRepository extends CrudRepository<Incident, Long> {
}
