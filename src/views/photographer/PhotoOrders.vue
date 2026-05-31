<template>
  <div>
    <h2 class="section-title">订单管理</h2>
    <p class="section-desc">用户确定摄影师后订单进入拍摄流程，完成后标记订单</p>
    <el-table :data="orders" stripe v-loading="loading">
      <el-table-column prop="orderNo" label="订单编号" width="150" />
      <el-table-column prop="serverName" label="服务" />
      <el-table-column prop="serverTime" label="拍摄时间" />
      <el-table-column prop="phone" label="用户联系方式" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="total" label="参考价">
        <template #default="{ row }">¥{{ row.total }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button
            v-if="row.status === ORDER_STATUS.CONFIRMED"
            type="success"
            size="small"
            @click="complete(row)"
          >标记完成</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, updateOrderStatus } from '@/api/orders'
import { ORDER_STATUS } from '@/constants'

const orders = ref([])
const loading = ref(false)

function statusType(s) {
  const map = {
    [ORDER_STATUS.CONTACTED]: 'warning',
    [ORDER_STATUS.CONFIRMED]: 'success',
    [ORDER_STATUS.DONE]: 'success',
    [ORDER_STATUS.CANCELLED]: 'info'
  }
  return map[s] || ''
}

async function load() {
  loading.value = true
  try {
    orders.value = await getOrders()
  } finally {
    loading.value = false
  }
}

async function complete(row) {
  await ElMessageBox.confirm('确认该订单拍摄已完成？', '完成订单')
  await updateOrderStatus(row.id, ORDER_STATUS.DONE)
  ElMessage.success('订单已标记完成')
  await load()
}

onMounted(load)
</script>
