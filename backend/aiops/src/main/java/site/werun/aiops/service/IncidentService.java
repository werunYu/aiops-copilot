package site.werun.aiops.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import site.werun.aiops.domain.Incident;
import site.werun.aiops.domain.IncidentRepository;
import site.werun.aiops.enums.IncidentStatusEnum;
import site.werun.aiops.exception.IncidentNotFoundException;
import site.werun.aiops.request.CreateIncidentRequest;

import java.util.Optional;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/14 06:48
 * @description
 **/
@Slf4j
@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public Incident save(CreateIncidentRequest request) {
        Incident incident = Incident.of(
                request.getServiceName(),
                request.getEnvironment(),
                request.getTitle(),
                request.getRawAlert(),
                IncidentStatusEnum.PENDING.getStatus()
        );
        return incidentRepository.save(incident);
    }

    public Page<Incident> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return incidentRepository.findAll(pageable);
    }

    public Incident findById(Long id) {
        Optional<Incident> incident = incidentRepository.findById(id);
        if (incident.isEmpty()) {
            throw new IncidentNotFoundException(id);
        }

        return incident.get();
    }

    public Incident updateStatus(Long id, String newStatus) {
        log.info("更新事件:{}状态为:{}", id, newStatus);
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        Incident updated = incident.withStatus(newStatus);
        // Spring Data JDBC 根据 id 非空判断执行 UPDATE
        return incidentRepository.save(updated);
    }
}
