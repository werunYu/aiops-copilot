package site.werun.aiops.response;

import lombok.Getter;
import lombok.Setter;
import site.werun.aiops.enums.ErrorCodeEnum;

import java.io.Serializable;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/16 13:49
 * @description
 **/
@Setter
@Getter
public class Result<T> implements Serializable {

    private String code;

    private String message;

    private T data;

    public static <T> Result<T> success() {
        return new Result<>(ErrorCodeEnum.ERROR_CODE_SYSTEM_000000.getCode(), ErrorCodeEnum.ERROR_CODE_SYSTEM_000000.getDesc());
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCodeEnum.ERROR_CODE_SYSTEM_000000.getCode(), ErrorCodeEnum.ERROR_CODE_SYSTEM_000000.getDesc(), data);
    }

    public static Result<?> failed() {
        return new Result<>(ErrorCodeEnum.ERROR_CODE_SYSTEM_000001.getCode(), ErrorCodeEnum.ERROR_CODE_SYSTEM_000001.getDesc(), null);
    }

    public static Result<?> failed(String message) {
        return new Result<>(ErrorCodeEnum.ERROR_CODE_SYSTEM_000001.getCode(), message, null);
    }

    public static Result<?> failed(ErrorCodeEnum errorCodeEnum) {
        return new Result<>(errorCodeEnum.getCode(), errorCodeEnum.getDesc(), null);
    }

    public Result() {
    }

    public Result(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> instance(String code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}
