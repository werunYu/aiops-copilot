<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listIncidents } from '../api/incidents'
import type { Incident } from '../types/incident'

const router = useRouter()
const incidents = ref<Incident[]>([])
const loading = ref(false)
const total = ref(0)

async function load(page = 0) {
  loading.value = true
  try {
    const result = await listIncidents(page, 20)
    incidents.value = result.content
    total.value = result.page.totalElements
  } finally {
    loading.value = false
  }
}

onMounted(() => load())
</script>

<template>
  <el-card>
    <template #header>
      <div class="card-header"><strong>告警事件</strong><el-button type="primary" @click="router.push('/incidents/new')">创建告警</el-button></div>
    </template>
    <el-table :data="incidents" v-loading="loading" @row-click="(row: Incident) => router.push('/incidents/' + row.id)">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="serviceName" label="服务" />
      <el-table-column prop="environment" label="环境" width="120" />
      <el-table-column prop="title" label="告警标题" />
      <el-table-column prop="status" label="状态" width="120"><template #default="scope"><el-tag>{{ scope.row.status }}</el-tag></template></el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
    </el-table>
    <el-pagination :total="total" :page-size="20" layout="total" />
  </el-card>
</template>

<style scoped>.card-header { display: flex; justify-content: space-between; align-items: center; }</style>
