<script setup lang="ts">
import type { AgentEvent } from '../types/incident'

defineProps<{ events: AgentEvent[] }>()

const labels: Record<string, string> = {
  ANALYSIS_STARTED: '分析开始',
  ANALYSIS_COMPLETED: '分析完成',
  ANALYSIS_FAILED: '分析失败',
  TOOL_CALLED: '工具调用',
  TOOL_FAILED: '工具失败',
}
</script>

<template>
  <el-timeline v-if="events.length">
    <el-timeline-item v-for="event in events" :key="event.id" :timestamp="event.createdAt" :type="event.status === 'FAILED' ? 'danger' : 'primary'">
      <strong>{{ labels[event.eventType] ?? event.eventType }}</strong>
      <span v-if="event.toolName"> · {{ event.toolName }}</span>
      <p>{{ event.content }}</p>
    </el-timeline-item>
  </el-timeline>
  <el-empty v-else description="尚无分析事件" :image-size="72" />
</template>
