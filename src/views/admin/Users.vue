<template>
  <div>
    <h2 class="section-title">用户管理</h2>
    <el-table :data="userList" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="账号" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="role" label="角色">
        <template #default="{ row }">{{ ROLE_LABELS[row.role] }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="电话" />
      <el-table-column label="积分" width="120">
        <template #default="{ row }">{{ row.points ?? row.account ?? 0 }} 积分</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.role === 'USER'"
            type="primary"
            size="small"
            link
            @click="openPoints(row)"
          >调整积分</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="pointsVisible" title="调整用户积分" width="400px">
      <p class="dialog-user">{{ currentUser?.name }}（{{ currentUser?.username }}）</p>
      <el-form label-width="80px">
        <el-form-item label="当前积分">
          <span>{{ currentUser?.points ?? currentUser?.account ?? 0 }}</span>
        </el-form-item>
        <el-form-item label="新积分" required>
          <el-input-number v-model="newPoints" :min="0" :max="1000000" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointsVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="savePoints">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchAdminUsers } from '@/api/catalog'
import { updateUserPoints } from '@/api/admin'
import { ROLE_LABELS } from '@/constants'

const userList = ref([])
const loading = ref(false)
const pointsVisible = ref(false)
const currentUser = ref(null)
const newPoints = ref(0)
const saving = ref(false)

function openPoints(row) {
  currentUser.value = row
  newPoints.value = row.points ?? row.account ?? 0
  pointsVisible.value = true
}

async function savePoints() {
  if (newPoints.value == null || newPoints.value < 0) {
    return ElMessage.warning('请输入有效积分')
  }
  saving.value = true
  try {
    const updated = await updateUserPoints(currentUser.value.id, newPoints.value)
    const idx = userList.value.findIndex(u => u.id === updated.id)
    if (idx >= 0) userList.value[idx] = updated
    ElMessage.success('积分已更新')
    pointsVisible.value = false
  } catch { /* */ } finally {
    saving.value = false
  }
}

async function load() {
  loading.value = true
  try {
    userList.value = await fetchAdminUsers()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.dialog-user { margin-bottom: 16px; color: var(--color-text-muted); }
</style>
