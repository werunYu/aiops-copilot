package site.werun.aiops.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/** Provides CRUD access to agent events. */
public interface AgentEventRepository extends PagingAndSortingRepository<AgentEvent, Long>, CrudRepository<AgentEvent, Long> {

    List<AgentEvent> findByIncidentIdOrderByCreatedAtAsc(Long incidentId);
}
