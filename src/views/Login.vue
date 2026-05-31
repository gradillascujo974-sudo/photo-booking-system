<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1 class="auth-title">光影约拍</h1>
      <p class="auth-sub">摄影约拍系统 · 登录</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="onSubmit">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="如 user123 / photo01 / admin" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="选择登录角色" style="width: 100%">
            <el-option label="普通用户" value="USER" />
            <el-option label="摄影师" value="PHOTOGRAPHER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-button type="primary" native-type="submit" :loading="loading" style="width: 100%; margin-top: 8px">
          登录
        </el-button>
      </el-form>
      <p class="auth-hint">
        演示账号：user123 / photo01 / admin，密码见论文测试用例
      </p>
      <router-link to="/register" class="auth-link">立即注册</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: 'user123',
  password: '123456',
  role: 'USER'
})
const rules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

async function onSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const user = await login(form)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect
    if (redirect) router.push(redirect)
    else if (user.role === 'ADMIN') router.push('/admin')
    else if (user.role === 'PHOTOGRAPHER') router.push('/photographer')
    else router.push('/')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f0f0f 0%, #1a1814 50%, #0f0f0f 100%);
  padding: 24px;
}
.auth-card {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background: var(--color-bg-card);
  border-radius: var(--radius);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow);
}
.auth-title {
  font-family: var(--font-display);
  font-size: 2rem;
  text-align: center;
  color: var(--color-accent);
}
.auth-sub {
  text-align: center;
  color: var(--color-text-muted);
  margin: 8px 0 28px;
  font-size: 0.9rem;
}
.auth-hint {
  margin-top: 16px;
  font-size: 0.75rem;
  color: var(--color-text-muted);
  line-height: 1.5;
}
.auth-link {
  display: block;
  text-align: center;
  margin-top: 16px;
  font-size: 0.9rem;
}
</style>
