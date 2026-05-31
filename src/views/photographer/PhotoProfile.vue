<template>
  <div>
    <h2 class="section-title">联系资料</h2>
    <p class="section-desc">用户确定摄影师后将看到您的手机号、微信与收款码，用于线下支付定金</p>
    <el-card v-loading="loading">
      <el-form :model="form" label-width="100px" style="max-width: 520px">
        <el-form-item label="手机号" required>
          <el-input v-model="form.phone" placeholder="用户联系您的手机号" />
        </el-form-item>
        <el-form-item label="微信号" required>
          <el-input v-model="form.wechat" placeholder="您的微信号" />
        </el-form-item>
        <el-form-item label="收款码">
          <div class="upload-area">
            <img v-if="form.payQrcode" :src="resolveImg(form.payQrcode)" alt="收款码" class="preview" />
            <el-upload
              :show-file-list="false"
              accept="image/*"
              :http-request="handleUpload"
            >
              <el-button type="primary">{{ form.payQrcode ? '更换收款码' : '上传收款码' }}</el-button>
            </el-upload>
          </div>
          <p class="hint">支持微信/支付宝收款码图片，用户确定合作后可扫码支付定金</p>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.introduce" type="textarea" rows="3" />
        </el-form-item>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPhotographerProfile, updatePhotographerProfile } from '@/api/photographer'
import { uploadImage } from '@/api/upload'
import { resolveImg } from '@/utils/img'

const loading = ref(false)
const saving = ref(false)
const form = ref({ phone: '', wechat: '', payQrcode: '', introduce: '' })

onMounted(async () => {
  loading.value = true
  try {
    const profile = await getPhotographerProfile()
    form.value = {
      phone: profile.phone || '',
      wechat: profile.wechat || '',
      payQrcode: profile.payQrcode || '',
      introduce: profile.introduce || ''
    }
  } finally {
    loading.value = false
  }
})

async function handleUpload({ file }) {
  const url = await uploadImage(file)
  form.value.payQrcode = url
  ElMessage.success('收款码已上传')
}

async function save() {
  if (!form.value.phone || !form.value.wechat) {
    return ElMessage.warning('请填写手机号和微信号')
  }
  saving.value = true
  try {
    await updatePhotographerProfile(form.value)
    ElMessage.success('资料已保存')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.section-desc { color: var(--color-text-muted); margin-bottom: 16px; }
.upload-area { display: flex; align-items: center; gap: 16px; flex-wrap: wrap; }
.preview { width: 120px; height: 120px; object-fit: contain; border: 1px solid var(--color-border); border-radius: 8px; }
.hint { margin-top: 8px; font-size: 0.85rem; color: var(--color-text-muted); }
</style>
