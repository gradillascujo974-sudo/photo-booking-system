<template>
  <div class="dash-layout">
    <aside class="sidebar">
      <div class="sidebar-brand">摄影师工作台</div>
      <el-menu :default-active="route.path" router>
        <el-menu-item index="/photographer">数据概览</el-menu-item>
        <el-menu-item index="/photographer/orders">订单管理</el-menu-item>
        <el-menu-item index="/photographer/services">我的服务</el-menu-item>
        <el-menu-item index="/photographer/portfolios">作品集</el-menu-item>
        <el-menu-item index="/photographer/profile">联系资料</el-menu-item>
      </el-menu>
      <el-button class="back-btn" text @click="$router.push('/')">返回前台</el-button>
    </aside>
    <div class="dash-content">
      <header class="dash-header">
        <span>{{ user?.name }} · 摄影师</span>
        <el-button size="small" @click="handleLogout">退出</el-button>
      </header>
      <main class="dash-main"><router-view /></main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUser } from '@/utils/storage'
import { logout } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const user = computed(() => getUser())

function handleLogout() {
  logout()
  router.push('/login')
}
</script>

<style scoped>
.dash-layout { display: flex; min-height: 100vh; }
.sidebar {
  width: 220px;
  background: var(--color-bg-soft);
  border-right: 1px solid var(--color-border);
  padding: 24px 0;
  display: flex;
  flex-direction: column;
}
.sidebar-brand {
  padding: 0 20px 20px;
  font-family: var(--font-display);
  font-weight: 600;
  color: var(--color-accent);
}
.back-btn { margin: auto 16px 16px; }
.dash-content { flex: 1; display: flex; flex-direction: column; }
.dash-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid var(--color-border);
}
.dash-main { padding: 24px; flex: 1; }
</style>
