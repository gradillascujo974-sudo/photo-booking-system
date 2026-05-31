<template>
  <div class="main-layout">
    <header class="site-header">
      <router-link to="/" class="logo">
        <span class="logo-icon">◉</span>
        <span>光影约拍</span>
      </router-link>
      <nav class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link to="/services">约拍服务</router-link>
        <router-link to="/photographers">摄影师</router-link>
        <router-link to="/portfolios">作品集</router-link>
      </nav>
      <div class="header-actions">
        <template v-if="user">
          <router-link v-if="user.role === 'USER'" to="/orders">我的订单</router-link>
          <el-dropdown trigger="click">
            <span class="user-trigger">
              <el-avatar :size="32">{{ user.name?.[0] }}</el-avatar>
              {{ user.name }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item v-if="user.role === 'USER'" @click="$router.push('/identify')">摄影师认证</el-dropdown-item>
                <el-dropdown-item v-if="user.role === 'PHOTOGRAPHER'" @click="$router.push('/photographer')">摄影师后台</el-dropdown-item>
                <el-dropdown-item v-if="user.role === 'ADMIN'" @click="$router.push('/admin')">管理后台</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button text @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </header>
    <main class="site-main">
      <router-view />
    </main>
    <footer class="site-footer">
      <p>基于 Spring Boot + Vue3 的摄影约拍系统 · 毕业设计演示</p>
      <p class="muted">普通用户 · 摄影师 · 管理员 三端协同</p>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { getUser, setUser } from '@/utils/storage'
import { logout } from '@/api/auth'

const router = useRouter()
const user = computed(() => getUser())

function handleLogout() {
  logout()
  setUser(null)
  router.push('/login')
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.site-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 32px;
  padding: 16px 32px;
  background: rgba(15, 15, 15, 0.92);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border);
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: var(--font-display);
  font-size: 1.25rem;
  color: var(--color-text);
  font-weight: 600;
}
.logo-icon { color: var(--color-accent); }
.nav-links {
  display: flex;
  gap: 24px;
  flex: 1;
}
.nav-links a {
  color: var(--color-text-muted);
  font-size: 0.95rem;
  transition: color 0.2s;
}
.nav-links a.router-link-active,
.nav-links a:hover {
  color: var(--color-accent);
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}
.header-actions a {
  color: var(--color-text-muted);
  font-size: 0.9rem;
}
.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--color-text);
}
.site-main { flex: 1; }
.site-footer {
  padding: 32px;
  text-align: center;
  border-top: 1px solid var(--color-border);
  color: var(--color-text-muted);
  font-size: 0.85rem;
}
.site-footer .muted { margin-top: 4px; opacity: 0.7; }
@media (max-width: 768px) {
  .site-header { flex-wrap: wrap; padding: 12px 16px; }
  .nav-links { order: 3; width: 100%; overflow-x: auto; }
}
</style>
