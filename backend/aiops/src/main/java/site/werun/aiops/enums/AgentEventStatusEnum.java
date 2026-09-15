package site.werun.aiops.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/15 08:37
 * @description
 **/
@Getter
@AllArgsConstructor
public enum AgentEventStatusEnum {

    ANALYSIS_STARTED("ANALYSIS_STARTED", "分析开始"),

    ANALYSIS_COMPLETED("ANALYSIS_COMPLETED", "分析完成"),

    ANALYSIS_FAILED("ANALYSIS_FAILED", "分析失败");

    private final String status;

    private final String desc;
}
