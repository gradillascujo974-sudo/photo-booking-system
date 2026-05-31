<template>
  <div>
    <h2 class="section-title">订单监控</h2>
    <el-table :data="orders" stripe v-loading="loading">
      <el-table-column prop="orderNo" label="订单编号" width="150" />
      <el-table-column prop="serverName" label="服务" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="photographerId" label="摄影师ID" width="90" />
      <el-table-column prop="total" label="金额">
        <template #default="{ row }">¥{{ row.total }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="time" label="下单时间" width="160" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrders } from '@/api/orders'

const orders = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    orders.value = await getOrders()
  } finally {
    loading.value = false
  }
})
</script>
