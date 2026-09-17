package site.werun.aiops.web;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import site.werun.aiops.domain.RcaReport;
import site.werun.aiops.dto.RcaAnalyzeReport;
import site.werun.aiops.service.AgentEventService;
import site.werun.aiops.service.IncidentAnalysisService;
import site.werun.aiops.service.IncidentService;
import site.werun.aiops.service.RcaReportService;
import site.werun.aiops.utils.JsonUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class IncidentReportControllerTest {

    @Test
    void report_endpoint_reads_json_string_stored_by_mysql_json_column() throws Exception {
        RcaReportService reportService = mock(RcaReportService.class);
        RcaAnalyzeReport report = new RcaAnalyzeReport("连接池耗尽", "HIGH", List.of(), List.of(), List.of(), List.of());
        String mysqlJsonString = JsonUtils.toJson(JsonUtils.toJson(report));
        when(reportService.findById(42L)).thenReturn(new RcaReport(
                1L, 42L, mysqlJsonString, "qwen", 321L, LocalDateTime.of(2026, 9, 17, 12, 0)
        ));
        MockMvc mockMvc = standaloneSetup(new IncidentController(
                mock(IncidentService.class), mock(IncidentAnalysisService.class), reportService, mock(AgentEventService.class)
        )).build();

        mockMvc.perform(get("/api/incidents/42/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.report.incidentSummary").value("连接池耗尽"));
    }
}
