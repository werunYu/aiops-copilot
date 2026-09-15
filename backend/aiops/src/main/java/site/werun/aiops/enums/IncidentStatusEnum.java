package site.werun.aiops.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/15 08:06
 * @description 事件状态枚举值.
 **/
@Getter
@AllArgsConstructor
public enum IncidentStatusEnum {
    ANALYZING("ANALYZING", "分析中"),
    COMPLETED("COMPLETED", "分析完成"),
    FAILED("FAILED", "分析失败");

    private final String status;

    private final String desc;
}
