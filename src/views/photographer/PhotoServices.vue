<template>
  <div>
    <h2 class="section-title">我的服务</h2>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="name" label="服务名称" />
      <el-table-column prop="style" label="风格" />
      <el-table-column prop="price" label="价格">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="saleCount" label="销量" />
      <el-table-column prop="status" label="状态" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchMyServices } from '@/api/catalog'

const list = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    list.value = await fetchMyServices()
  } finally {
    loading.value = false
  }
})
</script>
