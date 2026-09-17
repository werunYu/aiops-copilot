<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import { createIncident } from '../api/incidents'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive({ serviceName: '', environment: 'production', title: '', rawAlert: '' })
const rules: FormRules = {
  serviceName: [{ required: true, message: '服务名称不能为空', trigger: 'blur' }],
  environment: [{ required: true, message: '环境不能为空', trigger: 'blur' }],
  title: [{ required: true, message: '告警标题不能为空', trigger: 'blur' }],
  rawAlert: [{ required: true, message: '告警内容不能为空', trigger: 'blur' }],
}

async function submit() {
  if (!(await formRef.value?.validate().catch(() => false))) return
  submitting.value = true
  try {
    const incident = await createIncident(form)
    ElMessage.success('告警已创建')
    router.push('/incidents/' + incident.id)
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '创建失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <el-card style="max-width: 760px; margin: auto">
    <template #header><strong>创建告警</strong></template>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="服务名称" prop="serviceName"><el-input v-model="form.serviceName" placeholder="例如 order-service" /></el-form-item>
      <el-form-item label="环境" prop="environment"><el-input v-model="form.environment" /></el-form-item>
      <el-form-item label="告警标题" prop="title"><el-input v-model="form.title" /></el-form-item>
      <el-form-item label="告警内容" prop="rawAlert"><el-input v-model="form.rawAlert" type="textarea" :rows="6" /></el-form-item>
      <el-form-item><el-button @click="router.back()">取消</el-button><el-button type="primary" :loading="submitting" @click="submit">创建并查看</el-button></el-form-item>
    </el-form>
  </el-card>
</template>
