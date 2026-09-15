package site.werun.aiops.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.werun.aiops.domain.RcaReport;
import site.werun.aiops.domain.RcaReportRepository;
import site.werun.aiops.dto.RcaAnalyzeReport;
import site.werun.aiops.exception.RcaReportNotFoundException;
import site.werun.aiops.utils.JsonUtils;

import java.util.Optional;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/15 08:34
 * @description
 **/
@Slf4j
@Service
public class RcaReportService {

    private final RcaReportRepository rcaReportRepository;

    public RcaReportService(RcaReportRepository rcaReportRepository) {
        this.rcaReportRepository = rcaReportRepository;
    }

    /**
     * 保存事件分析报告.
     * @param id 事件id
     * @param rcaAnalyzeReport 分析报告
     * @param modelName 模型名称
     * @param duration 模型分析耗时.
     */
    public void save(Long id, RcaAnalyzeReport rcaAnalyzeReport, String modelName, long duration) {
        RcaReport rcaReport = RcaReport.of(id, JsonUtils.toJson(rcaAnalyzeReport), modelName, duration);
        rcaReportRepository.save(rcaReport);
    }

    public RcaReport findById(Long incidentId) {
        Optional<RcaReport> recReport = rcaReportRepository.findTopByIncidentIdOrderByCreatedAtDesc(incidentId);
        if (recReport.isEmpty()) {
            throw new RcaReportNotFoundException(incidentId);
        }

        return recReport.get();
    }
}
