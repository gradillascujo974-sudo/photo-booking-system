<template>
  <div>
    <div class="head">
      <h2 class="section-title">作品集管理</h2>
      <div class="head-actions">
        <el-button @click="previewPublic">预览用户端展示</el-button>
        <el-button type="primary" @click="openForm()">新增作品</el-button>
      </div>
    </div>
    <p class="tip">提示：状态为「上架」的作品会同步出现在用户端「作品集」页面。</p>

    <el-table :data="list" stripe v-loading="loading">
      <el-table-column label="封面" width="100">
        <template #default="{ row }">
          <img :src="resolveImg(row.img)" class="thumb" alt="" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column prop="descr" label="简介" show-overflow-tooltip min-width="160" />
      <el-table-column prop="status" label="用户端展示" width="110">
        <template #default="{ row }">
          <el-tag :type="isPublicVisible(row) ? 'success' : 'info'" size="small">
            {{ isPublicVisible(row) ? '已展示' : '未展示' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === '上架' ? 'success' : 'info'" size="small">{{ row.status || '上架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="数据" width="140">
        <template #default="{ row }">浏览 {{ row.readCount }} · 赞 {{ row.likeCount }}</template>
      </el-table-column>
      <el-table-column prop="time" label="创建时间" width="160" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openForm(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="editing ? '编辑作品' : '新增作品'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="作品集名称" />
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
              <el-icon v-if="!uploading"><Plus /></el-icon>
              <span>{{ uploading ? '上传中…' : '上传本地图片' }}</span>
            </div>
          </el-upload>
          <p class="upload-tip">支持 JPG/PNG/GIF/WebP，最大 5MB，图片将存入数据库</p>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" :disabled="!form.img" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchMyPortfolios } from '@/api/catalog'
import { createPortfolio, updatePortfolio, deletePortfolio } from '@/api/portfolios'
import { uploadImage } from '@/api/upload'
import { resolveImg } from '@/utils/img'
import { getUser } from '@/utils/storage'

const router = useRouter()
const list = ref([])
const loading = ref(false)
const visible = ref(false)
const saving = ref(false)
const uploading = ref(false)
const editing = ref(null)
const form = ref({ name: '', descr: '', img: '' })

function isPublicVisible(row) {
  return !row.status || row.status === '上架'
}

function previewPublic() {
  const user = getUser()
  router.push({ path: '/portfolios', query: { photographerId: user?.id } })
}

async function load() {
  loading.value = true
  try {
    list.value = await fetchMyPortfolios()
  } finally {
    loading.value = false
  }
}

function openForm(row) {
  editing.value = row || null
  form.value = row
    ? { name: row.name, descr: row.descr || '', img: row.img }
    : { name: '', descr: '', img: '' }
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
      await updatePortfolio(editing.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createPortfolio(form.value)
      ElMessage.success('创建成功')
    }
    visible.value = false
    await load()
  } catch { /* */ } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除「${row.name}」？`, '提示', { type: 'warning' })
  await deletePortfolio(row.id)
  ElMessage.success('已删除')
  await load()
}

onMounted(load)
</script>

<style scoped>
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.head-actions { display: flex; gap: 8px; }
.tip {
  color: var(--color-text-muted);
  font-size: 0.85rem;
  margin-bottom: 12px;
}
.thumb {
  width: 64px;
  height: 48px;
  object-fit: cover;
  border-radius: 6px;
}
.uploader :deep(.el-upload) {
  width: 100%;
}
.upload-placeholder,
.upload-preview {
  width: 100%;
  height: 160px;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--color-text-muted);
  cursor: pointer;
  overflow: hidden;
  position: relative;
}
.upload-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.upload-mask {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.45);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}
.upload-preview:hover .upload-mask { opacity: 1; }
.upload-tip {
  margin-top: 8px;
  font-size: 0.75rem;
  color: var(--color-text-muted);
}
</style>
