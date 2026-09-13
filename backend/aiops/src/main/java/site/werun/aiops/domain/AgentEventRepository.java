package site.werun.aiops.domain;

import org.springframework.data.repository.CrudRepository;

/** Provides CRUD access to agent events. */
public interface AgentEventRepository extends CrudRepository<AgentEvent, Long> {
}
