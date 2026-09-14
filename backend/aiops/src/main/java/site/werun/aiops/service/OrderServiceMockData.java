package site.werun.aiops.service;

import org.springframework.stereotype.Component;
import site.werun.aiops.dto.DeploymentRecord;
import site.werun.aiops.dto.LogEntry;
import site.werun.aiops.dto.ServiceMetrics;

import java.util.List;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/14 20:36
 * @description
 **/
@Component
public class OrderServiceMockData {

    private static final String ORDER_SERVICE = "order-service";

    public ServiceMetrics metrics(String serviceName) {
        verifyService(serviceName);

        return new ServiceMetrics(
                ORDER_SERVICE, 45.0, 70.0, 2350, 8.2,
                95, 100, 35
        );
    }

    public List<LogEntry> errorLogs(String serviceName, String keyword) {
        verifyService(serviceName);

        List<LogEntry> logs = List.of(
                new LogEntry(
                        "2026-09-14T09:42:13",
                        "ERROR",
                        "HikariPool-1 - Connection is not available, request timed out after 30000ms"
                ),
                new LogEntry(
                        "2026-09-14T09:42:15",
                        "ERROR",
                        "Timeout waiting for database connection while querying order details"
                )
        );

        if (keyword == null || keyword.isBlank()) {
            return logs;
        }

        String normalizedKeyword = keyword.toLowerCase();
        return logs.stream()
                .filter(log -> log.message().toLowerCase().contains(normalizedKeyword))
                .toList();
    }

    public List<DeploymentRecord> deployments(String serviceName) {
        verifyService(serviceName);

        return List.of(new DeploymentRecord(
                "2026-09-14T09:30:00",
                "v1.8.2",
                "增加订单查询关联逻辑"
        ));
    }

    private void verifyService(String serviceName) {
        if (!ORDER_SERVICE.equalsIgnoreCase(serviceName)) {
            throw new IllegalArgumentException(
                    "当前 MVP 仅提供 order-service 的 Mock 数据"
            );
        }
    }
}
