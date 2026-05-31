<template>
  <div class="dash-layout">
    <aside class="sidebar admin">
      <div class="sidebar-brand">管理后台</div>
      <el-menu :default-active="route.path" router>
        <el-menu-item index="/admin">数据概览</el-menu-item>
        <el-menu-item index="/admin/users">用户管理</el-menu-item>
        <el-menu-item index="/admin/identify">认证审核</el-menu-item>
        <el-menu-item index="/admin/orders">订单监控</el-menu-item>
        <el-menu-item index="/admin/portfolios">作品展示</el-menu-item>
        <el-menu-item index="/admin/notices">系统公告</el-menu-item>
      </el-menu>
      <el-button class="back-btn" text @click="$router.push('/')">返回前台</el-button>
    </aside>
    <div class="dash-content">
      <header class="dash-header">
        <span>管理员 · {{ user?.name }}</span>
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
.sidebar.admin .sidebar-brand { color: #e8a87c; }
.sidebar-brand {
  padding: 0 20px 20px;
  font-family: var(--font-display);
  font-weight: 600;
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
