package site.werun.aiops.web;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import site.werun.aiops.domain.Incident;
import site.werun.aiops.dto.RcaAnalyzeReport;
import site.werun.aiops.service.IncidentAnalysisService;
import site.werun.aiops.service.IncidentService;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/14 06:39
 * @description
 **/
@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    private final IncidentAnalysisService incidentAnalysisService;

    public IncidentController(
                              IncidentService incidentService,
                              IncidentAnalysisService incidentAnalysisService) {
        this.incidentService = incidentService;
        this.incidentAnalysisService = incidentAnalysisService;
    }

    /**
     * 提交事件.
     * @param incident {@link Incident}
     * @return 事件对象
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Incident post(@RequestBody Incident incident) {
        return incidentService.save(incident);
    }

    /**
     * 分页查询所有事件列表.
     * @param page 页数
     * @param pageSize 页面大小.
     * @return 事件列表
     */
    @GetMapping
    public PagedModel<Incident> get(@RequestParam("page") int page,
                                    @RequestParam("pageSize") int pageSize) {
        Page<Incident> result = incidentService.findAll(page, pageSize);
        return new PagedModel<>(result);
    }

    /**
     * 根据id查询事件详情.
     * @param id 事件id
     * @return 事件详情
     */
    @GetMapping("/{id}")
    public Incident findById(@PathVariable("id") Long id) {
        return incidentService.findById(id);
    }

    /**
     * 对事件进行分析.
     * @param id 事件id
     */
    @PostMapping("/{id}/analyze")
    public RcaAnalyzeReport analyze(@PathVariable("id") Long id) {
        return  incidentAnalysisService.analyze(id);
    }

}
