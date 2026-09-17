package site.werun.aiops.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.werun.aiops.domain.AgentEvent;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class SseEmitterRegistry implements AgentEventPublisher {

    private final ConcurrentHashMap<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long incidentId, List<AgentEvent> historicalEvents) {
        SseEmitter emitter = new SseEmitter(0L);
        historicalEvents.forEach(event -> send(emitter, event));
        emitters.computeIfAbsent(incidentId, ignored -> new CopyOnWriteArraySet<>()).add(emitter);
        emitter.onCompletion(() -> remove(incidentId, emitter));
        emitter.onTimeout(() -> remove(incidentId, emitter));
        emitter.onError(error -> remove(incidentId, emitter));
        return emitter;
    }

    @Override
    public void publish(AgentEvent event) {
        Set<SseEmitter> incidentEmitters = emitters.getOrDefault(event.incidentId(), Set.of());
        incidentEmitters.forEach(emitter -> send(emitter, event));
    }

    private void send(SseEmitter emitter, AgentEvent event) {
        try {
            emitter.send(SseEmitter.event().name("agent-event").data(event));
        } catch (IOException exception) {
            log.debug("SSE client disconnected while sending agent event", exception);
            emitter.complete();
        }
    }

    private void remove(Long incidentId, SseEmitter emitter) {
        emitters.computeIfPresent(incidentId, (ignored, values) -> {
            values.remove(emitter);
            return values.isEmpty() ? null : values;
        });
    }
}
