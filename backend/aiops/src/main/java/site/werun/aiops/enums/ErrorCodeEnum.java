package site.werun.aiops.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/15 13:28
 * @description 系统错误码.
 **/
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    ERROR_CODE_SYSTEM_000000("000000", "SUCCESS"),
    ERROR_CODE_SYSTEM_000001("000001", "系统异常"),

    ERROR_CODE_MODEL_010001("010001", "模型分析失败");

    private final String code;

    private final String desc;
}
