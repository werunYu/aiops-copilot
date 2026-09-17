package site.werun.aiops.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.werun.aiops.domain.AgentEvent;
import site.werun.aiops.domain.AgentEventRepository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    private final AgentEventPublisher agentEventPublisher;

    public AgentEventService(AgentEventRepository agentEventRepository, AgentEventPublisher agentEventPublisher) {
        this.agentEventRepository = agentEventRepository;
        this.agentEventPublisher = agentEventPublisher;
    }

    public List<AgentEvent> findEventByIncidentId(Long incidentId) {
        return agentEventRepository.findByIncidentIdOrderByCreatedAtAsc(incidentId);
    }

    public SseEmitter subscribe(Long incidentId) {
        return agentEventPublisher.subscribe(incidentId, findEventByIncidentId(incidentId));
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
        AgentEvent savedEvent = agentEventRepository.save(event);
        agentEventPublisher.publish(savedEvent);
        return savedEvent;
    }
}
