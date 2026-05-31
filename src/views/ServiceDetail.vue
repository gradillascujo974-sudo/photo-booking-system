<template>
  <div v-if="service" class="page-container detail">
    <el-row :gutter="32">
      <el-col :xs="24" :md="14">
        <img class="detail-img" :src="service.img" :alt="service.name" />
      </el-col>
      <el-col :xs="24" :md="10">
        <h1>{{ service.name }}</h1>
        <p class="desc">{{ service.descr }}</p>
        <div class="tags">
          <el-tag>{{ service.style }}</el-tag>
          <el-tag type="info">{{ categoryName }}</el-tag>
        </div>
        <p class="price">参考价 ¥{{ service.price }}</p>
        <p v-if="photographer" class="photo-link">
          摄影师：{{ photographer.name }}
          <el-tag v-if="photographer.certified" type="success" size="small">已认证</el-tag>
        </p>
        <p class="content-block">{{ service.content }}</p>
        <el-alert type="info" :closable="false" show-icon class="points-tip">
          <template #title>积分预约说明（{{ POINTS_CONFIG.RATE_DESC }}）</template>
          <p>提交意向不扣积分；平台联系摄影师扣 {{ POINTS_CONFIG.CONTACT }} 积分；确定摄影师扣 {{ POINTS_CONFIG.CONFIRM }} 积分，之后可查看收款码与联系方式，线下支付定金。</p>
        </el-alert>
        <div class="actions">
          <el-button type="primary" size="large" @click="openBook">提交预约意向</el-button>
          <el-button @click="toggleFav">{{ favorited ? '已收藏' : '收藏' }}</el-button>
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="bookVisible" title="提交预约意向" width="480px">
      <el-form :model="bookForm" label-width="90px">
        <el-form-item label="拍摄时间" required>
          <el-date-picker v-model="bookForm.serverTime" type="datetime" placeholder="选择时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm" />
        </el-form-item>
        <el-form-item label="联系方式" required>
          <el-input v-model="bookForm.phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="bookForm.remark" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="我的积分">
          <span class="points">{{ userPoints }} 积分</span>
          <span class="points-hint">联系摄影师需 {{ POINTS_CONFIG.CONTACT }} 积分，确定需 {{ POINTS_CONFIG.CONFIRM }} 积分</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookVisible = false">取消</el-button>
        <el-button type="primary" :loading="booking" @click="submitBook">提交意向</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { fetchServiceDetail, fetchCategories } from '@/api/catalog'
import { createOrder } from '@/api/orders'
import { toggleFavorite } from '@/api/favorites'
import { getUser } from '@/utils/storage'
import { POINTS_CONFIG } from '@/constants'

const route = useRoute()
const router = useRouter()
const service = ref(null)
const photographer = ref(null)
const categories = ref([])
const bookVisible = ref(false)
const booking = ref(false)
const favorited = ref(false)
const bookForm = ref({ serverTime: '', phone: '', remark: '' })

const user = computed(() => getUser())
const userPoints = computed(() => user.value?.points ?? user.value?.account ?? 0)
const categoryName = computed(() => {
  const c = categories.value.find(x => x.id === service.value?.typeId)
  return c?.name || ''
})

onMounted(async () => {
  categories.value = await fetchCategories()
  const data = await fetchServiceDetail(route.params.id)
  service.value = data.service
  photographer.value = data.photographer
  favorited.value = data.favorited
  const u = getUser()
  if (u) bookForm.value.phone = u.phone
})

function openBook() {
  if (!user.value) {
    ElMessage.warning('请先登录')
    return router.push({ name: 'Login', query: { redirect: route.fullPath } })
  }
  bookVisible.value = true
}

async function submitBook() {
  if (!bookForm.value.serverTime) return ElMessage.warning('请选择拍摄时间')
  booking.value = true
  try {
    await createOrder({
      serverId: service.value.id,
      serverTime: bookForm.value.serverTime,
      phone: bookForm.value.phone,
      remark: bookForm.value.remark
    })
    ElMessage.success('预约意向已提交，请到「我的订单」联系摄影师')
    bookVisible.value = false
    router.push('/orders')
  } catch {
    /* handled by interceptor */
  } finally {
    booking.value = false
  }
}

async function toggleFav() {
  if (!user.value) return ElMessage.warning('请先登录')
  const res = await toggleFavorite(service.value.id)
  favorited.value = res.favorited
  ElMessage.success(favorited.value ? '已收藏' : '已取消收藏')
}
</script>

<style scoped>
.detail-img {
  width: 100%;
  border-radius: var(--radius);
  max-height: 480px;
  object-fit: cover;
}
.detail h1 { font-family: var(--font-display); font-size: 1.75rem; margin: 16px 0 8px; }
.desc { color: var(--color-text-muted); margin-bottom: 12px; }
.tags { display: flex; gap: 8px; margin-bottom: 16px; }
.price { font-size: 1.5rem; color: var(--color-accent); font-weight: 600; margin: 16px 0; }
.photo-link { margin-bottom: 16px; }
.content-block { margin: 20px 0; color: var(--color-text-muted); line-height: 1.6; }
.points-tip { margin: 16px 0; }
.points-tip p { margin: 4px 0 0; font-size: 0.9rem; line-height: 1.5; }
.actions { display: flex; gap: 12px; margin-top: 16px; }
.points { color: var(--color-accent); font-weight: 600; font-size: 1.1rem; }
.points-hint { display: block; margin-top: 6px; font-size: 0.85rem; color: var(--color-text-muted); }
</style>
