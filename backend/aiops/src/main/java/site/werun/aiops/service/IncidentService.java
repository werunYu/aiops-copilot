package site.werun.aiops.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import site.werun.aiops.domain.Incident;
import site.werun.aiops.domain.IncidentRepository;
import site.werun.aiops.exception.IncidentNotFoundException;

import java.util.Optional;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/14 06:48
 * @description
 **/
@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public Incident save(Incident incident) {
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
}
