<template>
  <div>
    <h2 class="section-title">平台概览</h2>
    <el-row :gutter="16" class="stats">
      <el-col :span="6"><el-statistic title="注册用户" :value="stats.userCount" /></el-col>
      <el-col :span="6"><el-statistic title="摄影师" :value="stats.photoCount" /></el-col>
      <el-col :span="6"><el-statistic title="订单总数" :value="stats.orderCount" /></el-col>
      <el-col :span="6"><el-statistic title="待审核认证" :value="stats.pendingIdentify" /></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchAdminStats } from '@/api/catalog'

const stats = ref({ userCount: 0, photoCount: 0, orderCount: 0, pendingIdentify: 0 })

onMounted(async () => {
  stats.value = await fetchAdminStats()
})
</script>

<style scoped>
.stats { margin-top: 16px; }
</style>
