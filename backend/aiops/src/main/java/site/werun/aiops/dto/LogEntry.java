package site.werun.aiops.dto;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/14 20:36
 * @description
 **/
public record LogEntry(
        String timestamp,
        String level,
        String message) {
}
