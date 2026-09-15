package site.werun.aiops.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.werun.aiops.domain.AgentEvent;
import site.werun.aiops.domain.AgentEventRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/15 08:34
 * @description
 **/
@Slf4j
@Service
public class AgentEventService {

    private final AgentEventRepository agentEventRepository;

    public AgentEventService(AgentEventRepository agentEventRepository) {
        this.agentEventRepository = agentEventRepository;
    }

    public List<AgentEvent> findEventByIncidentId(Long incidentId) {
        return agentEventRepository.findByIncidentIdOrderByCreatedAtAsc(incidentId);
    }

    public AgentEvent save(
            Long incidentId,
            String eventType,
            String toolName,
            String content,
            String status) {
        AgentEvent event = new AgentEvent(
                null,
                incidentId,
                eventType,
                toolName,
                content,
                status,
                LocalDateTime.now()
        );
        return agentEventRepository.save(event);
    }
}
