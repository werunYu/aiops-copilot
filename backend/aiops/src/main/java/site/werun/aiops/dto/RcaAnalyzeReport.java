package site.werun.aiops.dto;

import site.werun.aiops.knowledge.KnowledgeReference;

import java.util.List;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/14 16:15
 * @description
 **/
public record RcaAnalyzeReport(
        String incidentSummary,
        String severity,
        List<PossibleRootCause> possibleRootCauses,
        List<Evidence> evidence,
        List<InvestigationStep> investigationSteps,
        List<KnowledgeReference> relatedKnowledge) {
}
