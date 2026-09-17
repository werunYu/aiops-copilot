<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getEvents, getIncident, getReport, openIncidentStream, startAnalysis } from '../api/incidents'
import type { AgentEvent, Incident, RcaReportResponse } from '../types/incident'
import AgentEventTimeline from '../components/AgentEventTimeline.vue'
import RcaReportCard from '../components/RcaReportCard.vue'

const props = defineProps<{ id: string }>()
const incident = ref<Incident>()
const events = ref<AgentEvent[]>([])
const report = ref<RcaReportResponse>()
const loading = ref(false)
let stream: EventSource | undefined

async function load() {
  const id = Number(props.id)
  incident.value = await getIncident(id)
  events.value = await getEvents(id)
  if (incident.value.status === 'COMPLETED') report.value = await getReport(id)
}

function connect() {
  stream?.close()
  stream = openIncidentStream(Number(props.id), async event => {
    if (!events.value.some(item => item.id === event.id)) events.value.push(event)
    if (event.eventType === 'ANALYSIS_COMPLETED' || event.eventType === 'ANALYSIS_FAILED') {
      stream?.close()
      await load()
    }
  })
}

async function analyze() {
  loading.value = true
  try {
    await startAnalysis(Number(props.id))
    await load()
    connect()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '分析启动失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => { await load(); if (incident.value?.status === 'ANALYZING') connect() })
onBeforeUnmount(() => stream?.close())
</script>

<template>
  <el-space direction="vertical" fill size="large" style="width: 100%" v-if="incident">
    <el-card><template #header><strong>事件 #{{ incident.id }}</strong></template>
      <el-descriptions :column="2" border><el-descriptions-item label="服务">{{ incident.serviceName }}</el-descriptions-item><el-descriptions-item label="环境">{{ incident.environment }}</el-descriptions-item><el-descriptions-item label="标题">{{ incident.title }}</el-descriptions-item><el-descriptions-item label="状态"><el-tag>{{ incident.status }}</el-tag></el-descriptions-item></el-descriptions>
      <p>{{ incident.rawAlert }}</p><el-button type="primary" :loading="loading" :disabled="incident.status === 'ANALYZING' || incident.status === 'COMPLETED'" @click="analyze">启动 AI 分析</el-button>
    </el-card>
    <el-card><template #header><strong>分析过程</strong></template><AgentEventTimeline :events="events" /></el-card>
    <RcaReportCard v-if="report" :report="report.report" />
  </el-space>
</template>
