<template>
  <div class="page-container">
    <h1 class="section-title">摄影师认证</h1>
    <p class="section-desc">提交身份信息与资质材料，等待管理员审核</p>

    <el-card v-if="record" class="status-card">
      <p>当前状态：<el-tag>{{ record.status }}</el-tag></p>
      <p v-if="record.reason" class="reason">审核意见：{{ record.reason }}</p>
    </el-card>

    <el-card style="max-width: 560px; margin-top: 24px">
      <el-form :model="form" label-width="100px" @submit.prevent="submit">
        <el-form-item label="身份证号" required>
          <el-input v-model="form.cardNo" placeholder="18位身份证号码" />
        </el-form-item>
        <el-form-item label="身份证照片">
          <el-input v-model="form.card" placeholder="图片URL或说明" />
        </el-form-item>
        <el-form-item label="资格证书">
          <el-input v-model="form.img" placeholder="证书图片URL" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="form.intro" type="textarea" rows="4" />
        </el-form-item>
        <el-button type="primary" native-type="submit" :loading="loading">提交认证申请</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyIdentify, submitIdentify } from '@/api/identify'

const form = ref({ cardNo: '', card: '', img: '', intro: '' })
const record = ref(null)
const loading = ref(false)

onMounted(async () => {
  try {
    record.value = await getMyIdentify()
    if (record.value) Object.assign(form.value, record.value)
  } catch {
    record.value = null
  }
})

async function submit() {
  loading.value = true
  try {
    record.value = await submitIdentify(form.value)
    ElMessage.success('提交成功，请等待审核')
  } catch { /* */ } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.status-card { margin-bottom: 16px; }
.reason { margin-top: 8px; color: var(--color-text-muted); }
</style>
