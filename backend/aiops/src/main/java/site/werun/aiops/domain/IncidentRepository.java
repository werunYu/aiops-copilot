package site.werun.aiops.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/** Provides CRUD access to incidents. */
public interface IncidentRepository extends PagingAndSortingRepository<Incident, Long>, CrudRepository<Incident, Long> {
}
