package site.werun.aiops.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import site.werun.aiops.dto.RcaAnalyzeReport;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/16 18:57
 * @description
 **/
@Data
public class RcaReportResponse implements Serializable {

    private Long incidentId;

    private RcaAnalyzeReport report;

    private String modelName;

    private Long durationMs;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
