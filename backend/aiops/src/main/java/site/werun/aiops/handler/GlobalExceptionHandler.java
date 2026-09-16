package site.werun.aiops.handler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.werun.aiops.exception.IncidentNotFoundException;
import site.werun.aiops.exception.RcaReportNotFoundException;
import site.werun.aiops.response.Result;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/16 13:47
 * @description
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IncidentNotFoundException.class)
    public Result handleIncidentNotFound(IncidentNotFoundException e) {
        return Result.failed("事件未查询到: " + e.getMessage());
    }

    @ExceptionHandler(RcaReportNotFoundException.class)
    public Result handleRcaReportNotFound(RcaReportNotFoundException e) {
        return Result.failed("根因分析报告未查询到: " + e.getMessage());
    }
}
