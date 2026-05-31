<template>
  <div>
    <div class="head">
      <h2 class="section-title">作品展示管理</h2>
      <el-button type="primary" @click="openForm()">新增展示作品</el-button>
    </div>
    <p class="desc">管理用户端「作品集」页面的展示内容，可控制上架/下架与排序权重。</p>

    <el-card shadow="never" class="filter-bar">
      <el-input v-model="filters.keyword" placeholder="搜索名称/简介" clearable style="width: 200px" @change="load" />
      <el-select v-model="filters.status" clearable placeholder="状态" style="width: 120px; margin-left: 12px" @change="load">
        <el-option label="上架" value="上架" />
        <el-option label="下架" value="下架" />
      </el-select>
      <el-select v-model="filters.photographerId" clearable placeholder="摄影师" style="width: 160px; margin-left: 12px" @change="load">
        <el-option v-for="p in photographers" :key="p.id" :label="p.name" :value="p.id" />
      </el-select>
    </el-card>

    <el-table :data="list" stripe v-loading="loading">
      <el-table-column label="封面" width="100">
        <template #default="{ row }">
          <img :src="resolveImg(row.img)" class="thumb" alt="" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column prop="photographerName" label="摄影师" width="100" />
      <el-table-column prop="descr" label="简介" show-overflow-tooltip min-width="140" />
      <el-table-column prop="sortOrder" label="排序" width="70" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === '上架' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="数据" width="130">
        <template #default="{ row }">{{ row.readCount }} 浏览 · {{ row.likeCount }} 赞</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="toggleStatus(row)">
            {{ row.status === '上架' ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" @click="openForm(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="editing ? '编辑作品' : '新增作品'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item v-if="!editing" label="摄影师" required>
          <el-select v-model="form.photographerId" placeholder="选择摄影师" style="width: 100%">
            <el-option v-for="p in photographers" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.descr" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="封面图" required>
          <el-upload
            class="uploader"
            :show-file-list="false"
            accept="image/jpeg,image/png,image/gif,image/webp"
            :http-request="handleUpload"
            :disabled="uploading"
          >
            <div v-if="form.img" class="upload-preview">
              <img :src="resolveImg(form.img)" alt="" />
              <div class="upload-mask">点击更换</div>
            </div>
            <div v-else class="upload-placeholder">
              <span>{{ uploading ? '上传中…' : '上传本地图片' }}</span>
            </div>
          </el-upload>
          <p class="upload-tip">支持 JPG/PNG/GIF/WebP，最大 5MB</p>
        </el-form-item>
        <el-form-item label="排序权重">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
          <span class="hint">数值越大越靠前</span>
        </el-form-item>
        <el-form-item label="展示状态">
          <el-radio-group v-model="form.status">
            <el-radio value="上架">上架</el-radio>
            <el-radio value="下架">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchPhotographers } from '@/api/catalog'
import {
  fetchAdminPortfolios,
  adminCreatePortfolio,
  adminUpdatePortfolio,
  adminDeletePortfolio
} from '@/api/portfolios'
import { uploadImage } from '@/api/upload'
import { resolveImg } from '@/utils/img'

const list = ref([])
const photographers = ref([])
const loading = ref(false)
const visible = ref(false)
const saving = ref(false)
const uploading = ref(false)
const editing = ref(null)
const filters = ref({ keyword: '', status: '', photographerId: null })
const form = ref({
  name: '', descr: '', img: '', status: '上架', sortOrder: 0, photographerId: null
})

async function load() {
  loading.value = true
  try {
    const params = {}
    if (filters.value.keyword) params.keyword = filters.value.keyword
    if (filters.value.status) params.status = filters.value.status
    if (filters.value.photographerId) params.photographerId = filters.value.photographerId
    list.value = await fetchAdminPortfolios(params)
  } finally {
    loading.value = false
  }
}

function openForm(row) {
  editing.value = row || null
  form.value = row
    ? {
        name: row.name,
        descr: row.descr || '',
        img: row.img,
        status: row.status || '上架',
        sortOrder: row.sortOrder || 0,
        photographerId: row.photographerId
      }
    : { name: '', descr: '', img: '', status: '上架', sortOrder: 0, photographerId: null }
  visible.value = true
}

async function handleUpload({ file }) {
  uploading.value = true
  try {
    const res = await uploadImage(file)
    form.value.img = res.url
    ElMessage.success('图片已上传')
  } catch { /* */ } finally {
    uploading.value = false
  }
}

async function save() {
  if (!form.value.img) return ElMessage.warning('请上传封面图')
  saving.value = true
  try {
    if (editing.value) {
      await adminUpdatePortfolio(editing.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await adminCreatePortfolio(form.value)
      ElMessage.success('创建成功')
    }
    visible.value = false
    await load()
  } catch { /* */ } finally {
    saving.value = false
  }
}

async function toggleStatus(row) {
  const status = row.status === '上架' ? '下架' : '上架'
  await adminUpdatePortfolio(row.id, { status })
  ElMessage.success(status === '上架' ? '已上架' : '已下架')
  await load()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除「${row.name}」？`, '提示', { type: 'warning' })
  await adminDeletePortfolio(row.id)
  ElMessage.success('已删除')
  await load()
}

onMounted(async () => {
  photographers.value = await fetchPhotographers()
  await load()
})
</script>

<style scoped>
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.desc {
  color: var(--color-text-muted);
  font-size: 0.9rem;
  margin-bottom: 16px;
}
.filter-bar { margin-bottom: 16px; }
.thumb {
  width: 64px;
  height: 48px;
  object-fit: cover;
  border-radius: 6px;
}
.preview {
  display: block;
  margin-top: 8px;
  max-width: 100%;
  max-height: 120px;
  border-radius: 8px;
}
.hint {
  margin-left: 8px;
  font-size: 0.8rem;
  color: var(--color-text-muted);
}
.uploader :deep(.el-upload) { width: 100%; }
.upload-placeholder, .upload-preview {
  width: 100%;
  height: 140px;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-muted);
  cursor: pointer;
  overflow: hidden;
  position: relative;
}
.upload-preview img { width: 100%; height: 100%; object-fit: cover; }
.upload-mask {
  position: absolute; inset: 0;
  background: rgba(0,0,0,0.45); color: #fff;
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity 0.2s;
}
.upload-preview:hover .upload-mask { opacity: 1; }
.upload-tip { margin-top: 8px; font-size: 0.75rem; color: var(--color-text-muted); }
</style>
