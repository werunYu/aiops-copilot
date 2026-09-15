package site.werun.aiops.tools;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import site.werun.aiops.dto.LogEntry;
import site.werun.aiops.service.AgentEventService;
import site.werun.aiops.service.OrderServiceMockData;
import site.werun.aiops.utils.JsonUtils;

import java.util.List;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/13 19:17
 * @description 日志查询Tool
 **/
@Component
public class LogQueryTool {

    private final OrderServiceMockData mockData;

    private final AgentEventService agentEventService;

    public LogQueryTool(OrderServiceMockData mockData, AgentEventService agentEventService) {
        this.mockData = mockData;
        this.agentEventService = agentEventService;
    }

    @Tool(
            name = "query_recent_error_logs",
            description = "查询服务近期错误日志。当监控指标异常、需要确认超时、连接池、数据库或异常堆栈证据时调用。"
    )
    public List<LogEntry> queryRecentErrorLogs(
            @ToolParam(description = "服务名称，例如 order-service")
            String serviceName,
            @ToolParam(description = "可选日志关键词，例如 HikariPool 或 timeout", required = false)
            String keyword,
            ToolContext toolContext) {

        Long incidentId = ((Number) toolContext.getContext()
                .get("incidentId"))
                .longValue();

        List<LogEntry> logList = mockData.errorLogs(serviceName, keyword);
        agentEventService.save(
                incidentId,
                "TOOL_CALLED",
                "query_recent_error_logs",
                JsonUtils.toJson(logList),
                "SUCCESS"
        );

        return logList;
    }
}
