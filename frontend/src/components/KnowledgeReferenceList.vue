<script setup lang="ts">
import type { KnowledgeReference } from '../types/incident'
defineProps<{ references: KnowledgeReference[] }>()
</script>

<template>
  <el-space direction="vertical" fill style="width: 100%">
    <el-card v-for="reference in references" :key="reference.source" shadow="never">
      <template #header>{{ reference.title }} <el-tag size="small">{{ reference.source }}</el-tag></template>
      {{ reference.excerpt }}
    </el-card>
  </el-space>
</template>
