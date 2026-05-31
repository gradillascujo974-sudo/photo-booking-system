<template>
  <div class="page-container">
    <h1 class="section-title">个人中心</h1>
    <el-row :gutter="24">
      <el-col :md="10">
        <el-card>
          <el-form :model="form" label-width="80px">
            <el-form-item label="账号">{{ user?.username }}</el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="手机">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item v-if="isUser" label="积分">
              <span class="points">{{ userPoints }} 积分</span>
              <span class="points-hint">（{{ POINTS_CONFIG.RATE_DESC }}，联系摄影师 {{ POINTS_CONFIG.CONTACT }} 积分，确定摄影师 {{ POINTS_CONFIG.CONFIRM }} 积分）</span>
            </el-form-item>
            <el-button type="primary" @click="save">保存资料</el-button>
          </el-form>
        </el-card>

        <el-card v-if="isUser" class="recharge-card">
          <template #header>
            <div class="recharge-header">
              <span>充值积分</span>
              <span class="recharge-balance">当前 {{ userPoints }} 积分</span>
            </div>
          </template>
          <p class="recharge-desc">{{ POINTS_CONFIG.RATE_DESC }}，充值后可用于联系摄影师、确定合作等操作。</p>
          <div class="quick-amounts">
            <el-button
              v-for="n in quickAmounts"
              :key="n"
              :type="rechargeAmount === n ? 'primary' : 'default'"
              @click="selectAmount(n)"
            >{{ n }} 积分</el-button>
          </div>
          <el-form label-width="80px" class="recharge-form">
            <el-form-item label="自定义">
              <el-input-number
                v-model="rechargeAmount"
                :min="1"
                :max="100000"
                :step="10"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="应付">
              <span class="pay-amount">¥{{ rechargeAmount || 0 }}</span>
              <span class="pay-hint">（按 {{ POINTS_CONFIG.RATE_DESC }} 折算）</span>
            </el-form-item>
          </el-form>
          <el-button type="primary" size="large" :loading="recharging" class="recharge-btn" @click="doRecharge">
            立即充值
          </el-button>
        </el-card>
      </el-col>
      <el-col :md="14">
        <el-card>
          <template #header>我的收藏</template>
          <el-empty v-if="!favorites.length" description="暂无收藏" />
          <div v-else class="fav-list">
            <div v-for="f in favorites" :key="f.id" class="fav-item" @click="$router.push(`/services/${f.serverId}`)">
              服务 #{{ f.serverId }} · {{ f.time }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUser } from '@/utils/storage'
import { updateProfile, refreshUser, recharge } from '@/api/auth'
import { getFavorites } from '@/api/favorites'
import { POINTS_CONFIG } from '@/constants'

const user = ref(getUser())
const form = ref({ name: '', phone: '', email: '' })
const favorites = ref([])
const rechargeAmount = ref(100)
const recharging = ref(false)
const quickAmounts = [50, 100, 200, 500]

const userPoints = computed(() => user.value?.points ?? user.value?.account ?? 0)
const isUser = computed(() => user.value?.role === 'USER')

onMounted(async () => {
  user.value = await refreshUser()
  if (user.value) {
    form.value = { name: user.value.name, phone: user.value.phone, email: user.value.email }
    try {
      favorites.value = await getFavorites()
    } catch {
      favorites.value = []
    }
  }
})

function selectAmount(n) {
  rechargeAmount.value = n
}

async function save() {
  try {
    user.value = await updateProfile(form.value)
    ElMessage.success('保存成功')
  } catch { /* */ }
}

async function doRecharge() {
  const amount = Number(rechargeAmount.value)
  if (!amount || amount <= 0) {
    return ElMessage.warning('请输入有效的充值积分')
  }
  await ElMessageBox.confirm(
    `确认充值 ${amount} 积分（应付 ¥${amount}）？`,
    '充值确认'
  )
  recharging.value = true
  try {
    user.value = await recharge(amount)
    ElMessage.success(`充值成功，当前积分 ${userPoints.value}`)
  } catch { /* */ } finally {
    recharging.value = false
  }
}
</script>

<style scoped>
.points { color: var(--color-accent); font-weight: 600; font-size: 1.2rem; display: block; }
.points-hint { display: block; margin-top: 6px; font-size: 0.85rem; color: var(--color-text-muted); }
.recharge-card { margin-top: 16px; }
.recharge-header { display: flex; justify-content: space-between; align-items: center; }
.recharge-balance { font-size: 0.9rem; color: var(--color-accent); font-weight: 600; }
.recharge-desc { margin: 0 0 16px; font-size: 0.9rem; color: var(--color-text-muted); line-height: 1.5; }
.quick-amounts { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 16px; }
.recharge-form { margin-bottom: 8px; }
.pay-amount { color: var(--color-accent); font-weight: 600; font-size: 1.25rem; }
.pay-hint { margin-left: 8px; font-size: 0.85rem; color: var(--color-text-muted); }
.recharge-btn { width: 100%; }
.fav-item {
  padding: 12px;
  border-bottom: 1px solid var(--color-border);
  cursor: pointer;
  color: var(--color-text-muted);
}
.fav-item:hover { color: var(--color-accent); }
</style>
