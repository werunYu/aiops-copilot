package site.werun.aiops.tools;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ToolContext;
import site.werun.aiops.service.AgentEventService;
import site.werun.aiops.service.OrderServiceMockData;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MetricsToolTest {

    @Test
    void failed_metric_query_is_persisted_as_tool_failure() {
        OrderServiceMockData mockData = mock(OrderServiceMockData.class);
        AgentEventService eventService = mock(AgentEventService.class);
        ToolContext toolContext = mock(ToolContext.class);
        when(toolContext.getContext()).thenReturn(Map.of("incidentId", 42L));
        when(mockData.metrics("missing-service")).thenThrow(new IllegalArgumentException("没有数据"));
        MetricsTool tool = new MetricsTool(mockData, eventService);

        assertThatThrownBy(() -> tool.queryServiceMetrics("missing-service", toolContext))
                .isInstanceOf(IllegalArgumentException.class);

        verify(eventService).save(eq(42L), eq("TOOL_FAILED"), eq("query_service_metrics"), contains("没有数据"), eq("FAILED"));
    }
}
