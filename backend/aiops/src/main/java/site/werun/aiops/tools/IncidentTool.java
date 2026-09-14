package site.werun.aiops.tools;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import site.werun.aiops.domain.Incident;
import site.werun.aiops.domain.IncidentRepository;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/14 13:36
 * @description
 **/
@Slf4j
@Component
public class IncidentTool {

    private final IncidentRepository incidentRepository;

    public IncidentTool(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Tool(description = "保存告警信息", name = "保存告警信息")
    public void saveIncident(@ToolParam(description = "告警服务名称") String serviceName,
                             @ToolParam(description = "告警环境") String environment,
                             @ToolParam(description = "告警标题") String title,
                             @ToolParam(description = "告警内容") String rawAlert,
                             @ToolParam(description = "告警状态") String status) {

        log.info("开始调用保存告警信息工具");
        if (StringUtils.isBlank(environment)) {
            environment = "prod";
        }

        if (StringUtils.isBlank(title)) {
            title = "系统告警";
        }

        Incident incident = Incident.of(serviceName, environment, title, rawAlert, status);
        incidentRepository.save(incident);
    }
}
