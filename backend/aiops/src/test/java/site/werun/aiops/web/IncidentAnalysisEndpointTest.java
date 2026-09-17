package site.werun.aiops.web;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import site.werun.aiops.service.AgentEventService;
import site.werun.aiops.service.IncidentAnalysisService;
import site.werun.aiops.service.IncidentService;
import site.werun.aiops.service.RcaReportService;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class IncidentAnalysisEndpointTest {

    @Test
    void analysis_request_is_accepted_without_waiting_for_report() throws Exception {
        MockMvc mockMvc = standaloneSetup(new IncidentController(
                mock(IncidentService.class),
                mock(IncidentAnalysisService.class),
                mock(RcaReportService.class),
                mock(AgentEventService.class)
        )).build();

        mockMvc.perform(post("/api/incidents/42/analyze"))
                .andExpect(status().isAccepted());
    }
}
