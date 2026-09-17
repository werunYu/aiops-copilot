<script setup lang="ts">
import type { RcaReport } from '../types/incident'
import KnowledgeReferenceList from './KnowledgeReferenceList.vue'
defineProps<{ report: RcaReport }>()
</script>

<template>
  <el-card>
    <template #header><strong>RCA 分析报告</strong></template>
    <el-descriptions :column="1" border>
      <el-descriptions-item label="故障摘要">{{ report.incidentSummary }}</el-descriptions-item>
      <el-descriptions-item label="严重等级"><el-tag type="danger">{{ report.severity }}</el-tag></el-descriptions-item>
    </el-descriptions>
    <h3>可能根因</h3>
    <el-table :data="report.possibleRootCauses" size="small"><el-table-column prop="cause" label="根因" /><el-table-column prop="confidence" label="置信度" width="90" /><el-table-column prop="reasoning" label="依据" /></el-table>
    <h3>排查步骤</h3>
    <el-steps direction="vertical" :active="report.investigationSteps.length"><el-step v-for="step in report.investigationSteps" :key="step.order" :title="step.action" :description="step.purpose" /></el-steps>
    <h3>关联知识</h3>
    <KnowledgeReferenceList :references="report.relatedKnowledge" />
  </el-card>
</template>
