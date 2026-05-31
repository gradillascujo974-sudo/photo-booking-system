<template>
  <div>
    <h2 class="section-title">摄影师认证审核</h2>
    <el-input v-model="filterUser" placeholder="按用户名筛选" clearable style="max-width: 240px; margin-bottom: 16px" />
    <el-table :data="filtered" stripe v-loading="loading">
      <el-table-column prop="userName" label="申请人" />
      <el-table-column prop="cardNo" label="身份证号" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column prop="time" label="申请时间" width="160" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <template v-if="row.status === '待审核'">
            <el-button type="success" size="small" @click="pass(row)">通过</el-button>
            <el-button type="danger" size="small" @click="reject(row)">驳回</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getIdentifies, auditIdentify } from '@/api/identify'

const list = ref([])
const filterUser = ref('')
const loading = ref(false)

const filtered = computed(() => {
  if (!filterUser.value) return list.value
  return list.value.filter(i => i.userName?.includes(filterUser.value))
})

async function load() {
  loading.value = true
  try {
    list.value = await getIdentifies()
  } finally {
    loading.value = false
  }
}

async function pass(row) {
  await auditIdentify(row.id, '通过', '')
  ElMessage.success('审核通过，用户已升级为摄影师')
  await load()
}

async function reject(row) {
  const { value } = await ElMessageBox.prompt('请输入驳回理由', '驳回认证')
  await auditIdentify(row.id, '已驳回', value || '材料不符合要求')
  ElMessage.success('已驳回')
  await load()
}

onMounted(load)
</script>
