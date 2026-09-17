package site.werun.aiops.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.werun.aiops.domain.AgentEvent;

import java.util.List;

public interface AgentEventPublisher {

    void publish(AgentEvent event);

    SseEmitter subscribe(Long incidentId, List<AgentEvent> historicalEvents);
}
