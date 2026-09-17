package site.werun.aiops.tools;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import site.werun.aiops.dto.ServiceMetrics;
import site.werun.aiops.service.AgentEventService;
import site.werun.aiops.service.OrderServiceMockData;
import site.werun.aiops.utils.JsonUtils;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/13 19:17
 * @description 指标分析Tool
 **/
@Component
public class MetricsTool {

    private final OrderServiceMockData mockData;

    private final AgentEventService agentEventService;

    public MetricsTool(OrderServiceMockData mockData, AgentEventService agentEventService) {
        this.mockData = mockData;
        this.agentEventService = agentEventService;
    }

    @Tool(
            name = "query_service_metrics",
            description = "查询指定服务的近期监控指标。需要判断延迟、错误率、CPU、内存、数据库连接池或 Redis 是否异常时调用。"
    )
    public ServiceMetrics queryServiceMetrics(
            @ToolParam(description = "服务名称，例如 order-service")
            String serviceName,
            ToolContext toolContext) {

        Long incidentId = ((Number) toolContext.getContext()
                .get("incidentId"))
                .longValue();

        try {
            ServiceMetrics result = mockData.metrics(serviceName);
            agentEventService.save(incidentId, "TOOL_CALLED", "query_service_metrics", JsonUtils.toJson(result), "SUCCESS");
            return result;
        } catch (RuntimeException exception) {
            agentEventService.save(incidentId, "TOOL_FAILED", "query_service_metrics", "调用失败: " + exception.getMessage(), "FAILED");
            throw exception;
        }
    }
}
