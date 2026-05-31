<template>
  <div>
    <h2 class="section-title">数据概览</h2>
    <el-row :gutter="16" class="stats">
      <el-col :span="6"><el-statistic title="待用户确定" :value="stats.contacted" /></el-col>
      <el-col :span="6"><el-statistic title="进行中" :value="stats.confirmed" /></el-col>
      <el-col :span="6"><el-statistic title="已完成" :value="stats.done" /></el-col>
      <el-col :span="6"><el-statistic title="订单总数" :value="orders.length" /></el-col>
    </el-row>
    <el-card style="margin-top: 24px">
      <template #header>近期订单</template>
      <el-table :data="recentOrders" size="small">
        <el-table-column prop="orderNo" label="订单号" />
        <el-table-column prop="serverName" label="服务" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="total" label="参考价">
          <template #default="{ row }">¥{{ row.total }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getOrders } from '@/api/orders'
import { ORDER_STATUS } from '@/constants'

const orders = ref([])

const stats = computed(() => ({
  contacted: orders.value.filter(o => o.status === ORDER_STATUS.CONTACTED).length,
  confirmed: orders.value.filter(o => o.status === ORDER_STATUS.CONFIRMED).length,
  done: orders.value.filter(o => o.status === ORDER_STATUS.DONE).length
}))

const recentOrders = computed(() => orders.value.slice(0, 5))

onMounted(async () => {
  orders.value = await getOrders()
})
</script>

<style scoped>
.stats { margin-top: 16px; }
</style>
