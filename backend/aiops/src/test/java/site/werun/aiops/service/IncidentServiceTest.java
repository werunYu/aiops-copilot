package site.werun.aiops.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import site.werun.aiops.domain.Incident;
import site.werun.aiops.domain.IncidentRepository;
import site.werun.aiops.request.CreateIncidentRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IncidentServiceTest {

    @Test
    void creating_incident_starts_in_pending_state() {
        IncidentRepository repository = mock(IncidentRepository.class);
        when(repository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));
        IncidentService service = new IncidentService(repository);

        Incident incident = service.save(request("order-service", "production", "订单接口超时", "P99 超过 2 秒"));

        assertThat(incident.status()).isEqualTo("PENDING");
    }

    @Test
    void incident_request_rejects_blank_required_fields() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        CreateIncidentRequest request = request("", "", "", "");

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("serviceName", "environment", "title", "rawAlert");
    }

    private CreateIncidentRequest request(String serviceName, String environment, String title, String rawAlert) {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setServiceName(serviceName);
        request.setEnvironment(environment);
        request.setTitle(title);
        request.setRawAlert(rawAlert);
        return request;
    }
}
