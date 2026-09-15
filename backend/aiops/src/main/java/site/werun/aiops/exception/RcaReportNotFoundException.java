package site.werun.aiops.exception;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/15 14:35
 * @description
 **/
public class RcaReportNotFoundException extends RuntimeException {
    public RcaReportNotFoundException(Long incidentId) {
        super("事件" + incidentId + "暂无分析报告");
    }
}
