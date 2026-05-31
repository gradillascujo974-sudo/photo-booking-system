<template>
  <div class="page-container">
    <h1 class="section-title">我的订单</h1>
    <p class="section-desc">积分联系摄影师、确定合作并线下支付定金</p>

    <el-card shadow="never" class="filter-bar">
      <el-input v-model="filterName" placeholder="服务名称" clearable style="width: 180px" />
      <el-select v-model="filterStatus" clearable placeholder="订单状态" style="width: 140px; margin-left: 12px">
        <el-option v-for="v in statusOptions" :key="v" :label="v" :value="v" />
      </el-select>
      <span v-if="user" class="points-badge">当前积分：{{ userPoints }}</span>
    </el-card>

    <el-table :data="filtered" stripe style="width: 100%; margin-top: 16px" v-loading="loading">
      <el-table-column prop="orderNo" label="订单编号" width="160" />
      <el-table-column prop="serverName" label="服务" />
      <el-table-column prop="total" label="参考价">
        <template #default="{ row }">¥{{ row.total }}</template>
      </el-table-column>
      <el-table-column prop="serverTime" label="拍摄时间" width="160" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === ORDER_STATUS.WAIT_CONTACT"
            type="primary"
            size="small"
            @click="handleContact(row)"
          >联系摄影师（{{ POINTS_CONFIG.CONTACT }}积分）</el-button>
          <el-button
            v-if="row.status === ORDER_STATUS.CONTACTED"
            type="primary"
            size="small"
            @click="handleConfirm(row)"
          >确定摄影师（{{ POINTS_CONFIG.CONFIRM }}积分）</el-button>
          <el-button
            v-if="[ORDER_STATUS.CONFIRMED, ORDER_STATUS.DONE].includes(row.status)"
            size="small"
            @click="showContact(row)"
          >联系方式/收款码</el-button>
          <el-button
            v-if="canCancel(row.status)"
            size="small"
            @click="cancelOrder(row)"
          >取消</el-button>
          <el-button
            v-if="row.status === ORDER_STATUS.DONE && !hasReview(row.id)"
            type="primary"
            size="small"
            @click="openReview(row)"
          >评价</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="contactVisible" title="摄影师联系方式" width="440px">
      <div v-if="contactInfo" class="contact-panel">
        <p><strong>摄影师：</strong>{{ contactInfo.name }}</p>
        <p><strong>手机：</strong>{{ contactInfo.phone || '未填写' }}</p>
        <p><strong>微信：</strong>{{ contactInfo.wechat || '未填写' }}</p>
        <p class="deposit-tip">{{ contactInfo.depositTip }}</p>
        <div v-if="contactInfo.payQrcode" class="qrcode-wrap">
          <p><strong>收款码（定金）</strong></p>
          <img :src="resolveImg(contactInfo.payQrcode)" alt="收款码" class="qrcode" />
        </div>
        <el-empty v-else description="摄影师尚未上传收款码" />
      </div>
      <template #footer>
        <el-button type="primary" @click="contactVisible = false">知道了</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="reviewVisible" title="订单评价" width="440px">
      <el-form label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.score" />
        </el-form-item>
        <el-form-item label="评价内容" required>
          <el-input v-model="reviewForm.content" type="textarea" rows="4" placeholder="分享您的拍摄体验" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, updateOrderStatus, contactOrder, confirmOrder, getOrderContact } from '@/api/orders'
import { getReviews, createReview, canReviewOrder } from '@/api/reviews'
import { ORDER_STATUS, POINTS_CONFIG } from '@/constants'
import { refreshUser } from '@/api/auth'
import { getUser } from '@/utils/storage'
import { resolveImg } from '@/utils/img'

const orders = ref([])
const reviews = ref([])
const loading = ref(false)
const filterName = ref('')
const filterStatus = ref('')
const reviewVisible = ref(false)
const contactVisible = ref(false)
const contactInfo = ref(null)
const currentOrder = ref(null)
const reviewForm = ref({ score: 5, content: '' })
const user = ref(getUser())
const statusOptions = Object.values(ORDER_STATUS)

const userPoints = computed(() => user.value?.points ?? user.value?.account ?? 0)

const filtered = computed(() => {
  let list = [...orders.value]
  if (filterName.value) list = list.filter(o => o.serverName?.includes(filterName.value))
  if (filterStatus.value) list = list.filter(o => o.status === filterStatus.value)
  return list
})

function statusType(s) {
  const map = {
    [ORDER_STATUS.WAIT_CONTACT]: 'info',
    [ORDER_STATUS.CONTACTED]: 'warning',
    [ORDER_STATUS.CONFIRMED]: 'success',
    [ORDER_STATUS.DONE]: 'success',
    [ORDER_STATUS.CANCELLED]: 'info'
  }
  return map[s] || ''
}

function canCancel(status) {
  return [ORDER_STATUS.WAIT_CONTACT, ORDER_STATUS.CONTACTED, ORDER_STATUS.CONFIRMED].includes(status)
}

function hasReview(orderId) {
  return reviews.value.some(r => r.orderId === orderId)
}

async function handleContact(row) {
  await ElMessageBox.confirm(
    `平台将代为联系摄影师，消耗 ${POINTS_CONFIG.CONTACT} 积分（${POINTS_CONFIG.RATE_DESC}）`,
    '联系摄影师'
  )
  await contactOrder(row.id)
  user.value = await refreshUser()
  ElMessage.success('已联系摄影师，请确认是否合作')
  await load()
}

async function handleConfirm(row) {
  await ElMessageBox.confirm(
    `确定与该摄影师合作将消耗 ${POINTS_CONFIG.CONFIRM} 积分，之后可查看联系方式与收款码支付定金`,
    '确定摄影师'
  )
  const data = await confirmOrder(row.id)
  user.value = await refreshUser()
  contactInfo.value = data.contact
  contactVisible.value = true
  ElMessage.success('已确定摄影师，请扫码支付定金')
  await load()
}

async function showContact(row) {
  contactInfo.value = await getOrderContact(row.id)
  contactVisible.value = true
}

function openReview(row) {
  if (!canReviewOrder(row)) return ElMessage.warning('只有已完成订单可评价')
  currentOrder.value = row
  reviewForm.value = { score: 5, content: '' }
  reviewVisible.value = true
}

async function submitReview() {
  try {
    await createReview({
      orderId: currentOrder.value.id,
      score: reviewForm.value.score,
      content: reviewForm.value.content,
      serverId: currentOrder.value.serverId,
      photographerId: currentOrder.value.photographerId
    })
    ElMessage.success('评价成功')
    reviewVisible.value = false
    reviews.value = await getReviews()
  } catch { /* */ }
}

async function cancelOrder(row) {
  let msg = '确定取消该订单？'
  if (row.status === ORDER_STATUS.CONTACTED) {
    msg = `取消将退还 ${POINTS_CONFIG.CONTACT} 积分`
  } else if (row.status === ORDER_STATUS.CONFIRMED) {
    msg = `取消将退还 ${POINTS_CONFIG.CONTACT + POINTS_CONFIG.CONFIRM} 积分`
  }
  await ElMessageBox.confirm(msg, '提示')
  await updateOrderStatus(row.id, ORDER_STATUS.CANCELLED)
  await refreshUser()
  user.value = getUser()
  ElMessage.success('订单已取消')
  await load()
}

async function load() {
  loading.value = true
  try {
    orders.value = await getOrders()
    reviews.value = await getReviews()
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  user.value = await refreshUser()
  await load()
})
</script>

<style scoped>
.filter-bar { margin-bottom: 8px; display: flex; align-items: center; flex-wrap: wrap; gap: 8px; }
.points-badge { margin-left: auto; color: var(--color-accent); font-weight: 600; }
.contact-panel p { margin: 8px 0; }
.deposit-tip { color: var(--color-text-muted); font-size: 0.9rem; }
.qrcode-wrap { text-align: center; margin-top: 16px; }
.qrcode { max-width: 200px; border: 1px solid var(--color-border); border-radius: 8px; }
</style>
