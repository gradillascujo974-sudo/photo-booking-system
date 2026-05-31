import { createRouter, createWebHistory } from 'vue-router'
import { getUser } from '@/utils/storage'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { guest: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/Home.vue') },
      { path: 'services', name: 'Services', component: () => import('@/views/Services.vue') },
      { path: 'services/:id', name: 'ServiceDetail', component: () => import('@/views/ServiceDetail.vue') },
      { path: 'photographers', name: 'Photographers', component: () => import('@/views/Photographers.vue') },
      { path: 'portfolios', name: 'Portfolios', component: () => import('@/views/Portfolios.vue') },
      { path: 'portfolios/:id', name: 'PortfolioDetail', component: () => import('@/views/PortfolioDetail.vue') },
      { path: 'orders', name: 'Orders', component: () => import('@/views/Orders.vue'), meta: { auth: true } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { auth: true } },
      { path: 'identify', name: 'Identify', component: () => import('@/views/Identify.vue'), meta: { auth: true, role: 'USER' } }
    ]
  },
  {
    path: '/photographer',
    component: () => import('@/layouts/PhotographerLayout.vue'),
    meta: { auth: true, role: 'PHOTOGRAPHER' },
    children: [
      { path: '', name: 'PhotoDashboard', component: () => import('@/views/photographer/Dashboard.vue') },
      { path: 'orders', name: 'PhotoOrders', component: () => import('@/views/photographer/PhotoOrders.vue') },
      { path: 'services', name: 'PhotoServices', component: () => import('@/views/photographer/PhotoServices.vue') },
      { path: 'portfolios', name: 'PhotoPortfolios', component: () => import('@/views/photographer/PhotoPortfolios.vue') },
      { path: 'profile', name: 'PhotoProfile', component: () => import('@/views/photographer/PhotoProfile.vue') }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { auth: true, role: 'ADMIN' },
    children: [
      { path: '', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/Users.vue') },
      { path: 'identify', name: 'AdminIdentify', component: () => import('@/views/admin/Identify.vue') },
      { path: 'orders', name: 'AdminOrders', component: () => import('@/views/admin/Orders.vue') },
      { path: 'portfolios', name: 'AdminPortfolios', component: () => import('@/views/admin/Portfolios.vue') },
      { path: 'notices', name: 'AdminNotices', component: () => import('@/views/admin/Notices.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to, from, next) => {
  const user = getUser()
  if (to.meta.guest && user) {
    return next(getHomeByRole(user.role))
  }
  if (to.meta.auth && !user) {
    return next({ name: 'Login', query: { redirect: to.fullPath } })
  }
  if (to.meta.role && user && user.role !== to.meta.role) {
    return next(getHomeByRole(user.role))
  }
  next()
})

function getHomeByRole(role) {
  if (role === 'ADMIN') return '/admin'
  if (role === 'PHOTOGRAPHER') return '/photographer'
  return '/'
}

export default router
