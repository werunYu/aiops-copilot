<template>
  <el-container class="layout">
    <el-header class="header">
      <router-link to="/incidents">AIOps Copilot</router-link>
      <span>智能告警分析与根因定位</span>
    </el-header>
    <el-main><router-view /></el-main>
  </el-container>
</template>

<style>
body { margin: 0; background: #f5f7fa; color: #303133; font-family: Inter, "Microsoft YaHei", sans-serif; }
.layout { min-height: 100vh; }
.header { display: flex; align-items: center; gap: 16px; background: #1f2937; color: #d1d5db; }
.header a { color: #fff; font-size: 20px; font-weight: 700; text-decoration: none; }
</style>
