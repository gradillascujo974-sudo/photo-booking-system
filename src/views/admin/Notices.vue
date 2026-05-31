<template>
  <div>
    <h2 class="section-title">系统公告</h2>
    <el-button type="primary" style="margin-bottom: 16px" @click="openCreate">发布公告</el-button>
    <el-table :data="noticeList" stripe v-loading="loading">
      <el-table-column prop="title" label="标题" min-width="140" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip min-width="200" />
      <el-table-column prop="time" label="时间" width="120" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" link @click="removeNotice(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑公告' : '发布公告'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="form.content" type="textarea" rows="4" />
        </el-form-item>
        <el-form-item label="日期">
          <el-input v-model="form.time" placeholder="如 2026-05-01" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitNotice">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchNotices, createNotice } from '@/api/catalog'
import { updateAdminNotice, deleteAdminNotice } from '@/api/admin'

const noticeList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = ref({ title: '', content: '', time: '' })

function openCreate() {
  editingId.value = null
  form.value = { title: '', content: '', time: new Date().toISOString().slice(0, 10) }
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  form.value = { title: row.title, content: row.content, time: row.time }
  dialogVisible.value = true
}

async function submitNotice() {
  if (!form.value.title?.trim()) return ElMessage.warning('请输入标题')
  if (!form.value.content?.trim()) return ElMessage.warning('请输入内容')
  saving.value = true
  try {
    if (editingId.value) {
      await updateAdminNotice(editingId.value, form.value)
      ElMessage.success('公告已更新')
    } else {
      await createNotice(form.value)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    await load()
  } catch { /* */ } finally {
    saving.value = false
  }
}

async function removeNotice(row) {
  await ElMessageBox.confirm(`确定删除公告「${row.title}」？`, '删除确认', { type: 'warning' })
  await deleteAdminNotice(row.id)
  ElMessage.success('已删除')
  await load()
}

async function load() {
  loading.value = true
  try {
    noticeList.value = await fetchNotices()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
