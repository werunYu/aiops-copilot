package site.werun.aiops.tools;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import site.werun.aiops.dto.DeploymentRecord;
import site.werun.aiops.service.AgentEventService;
import site.werun.aiops.service.OrderServiceMockData;
import site.werun.aiops.utils.JsonUtils;

import java.util.List;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/13 19:18
 * @description 查询最近部署记录Tool
 **/
@Component
public class DeploymentTool {

    private final OrderServiceMockData mockData;

    private final AgentEventService agentEventService;

    public DeploymentTool(OrderServiceMockData mockData, AgentEventService agentEventService) {
        this.mockData = mockData;
        this.agentEventService = agentEventService;
    }

    @Tool(
            name = "query_recent_deployments",
            description = "查询指定服务近期部署记录。需要判断故障是否可能由近期发布或版本变更引起时调用。"
    )
    public List<DeploymentRecord> queryRecentDeployments(
            @ToolParam(description = "服务名称，例如 order-service")
            String serviceName,
            ToolContext toolContext) {

        Long incidentId = ((Number) toolContext.getContext()
                .get("incidentId"))
                .longValue();

        try {
            List<DeploymentRecord> deployments = mockData.deployments(serviceName);
            agentEventService.save(incidentId, "TOOL_CALLED", "query_recent_deployments", JsonUtils.toJson(deployments), "SUCCESS");
            return deployments;
        } catch (RuntimeException exception) {
            agentEventService.save(incidentId, "TOOL_FAILED", "query_recent_deployments", "调用失败: " + exception.getMessage(), "FAILED");
            throw exception;
        }
    }
}
