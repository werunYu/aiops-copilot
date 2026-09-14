package site.werun.aiops.dto;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/14 16:38
 * @description
 **/
public record ServiceMetrics(
        String service,
        double cpuUsage,
        double memoryUsage,
        int p99Latency,
        double errorRate,
        int dbPoolActive,
        int dbPoolMax,
        int redisLatency) {
}
