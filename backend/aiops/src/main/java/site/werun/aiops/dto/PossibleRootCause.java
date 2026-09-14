package site.werun.aiops.dto;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/14 16:15
 * @description
 **/
public record PossibleRootCause(String cause,
                                Integer confidence,
                                String reasoning) {
}
