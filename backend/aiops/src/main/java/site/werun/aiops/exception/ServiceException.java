package site.werun.aiops.exception;

import lombok.Getter;
import site.werun.aiops.enums.ErrorCodeEnum;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/15 13:27
 * @description
 **/
@Getter
public class ServiceException extends RuntimeException {
    private String code;
    private String message;

    public ServiceException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceException(ErrorCodeEnum errorCodeEnum) {
        this.code = errorCodeEnum.getCode();
        this.message = errorCodeEnum.getDesc();
    }

    public ServiceException(String message) {
        super(message);
    }
}
