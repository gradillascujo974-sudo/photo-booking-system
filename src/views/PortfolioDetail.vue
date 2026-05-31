<template>
  <div v-if="portfolio" class="page-container">
    <img class="hero-img" :src="resolveImg(portfolio.img)" :alt="portfolio.name" />
    <div class="detail-head">
      <div>
        <h1>{{ portfolio.name }}</h1>
        <p>{{ portfolio.descr }}</p>
        <p v-if="photographer" class="muted">摄影师：{{ photographer.name }}</p>
      </div>
      <el-button :type="liked ? 'primary' : 'default'" size="large" @click="handleLike">
        {{ liked ? '已点赞' : '点赞' }} ({{ likeCount }})
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { fetchPortfolioDetail } from '@/api/catalog'
import { toggleLike } from '@/api/likes'
import { resolveImg } from '@/utils/img'

const route = useRoute()
const portfolio = ref(null)
const photographer = ref(null)
const liked = ref(false)
const likeCount = ref(0)

onMounted(async () => {
  const data = await fetchPortfolioDetail(route.params.id)
  portfolio.value = data.portfolio
  photographer.value = data.photographer
  liked.value = data.liked
  likeCount.value = data.likeCount
})

async function handleLike() {
  try {
    const res = await toggleLike(portfolio.value.id)
    liked.value = res.liked
    likeCount.value = res.count
    ElMessage.success(res.liked ? '点赞成功' : '已取消点赞')
  } catch {
    /* interceptor */
  }
}
</script>

<style scoped>
.hero-img {
  width: 100%;
  max-height: 520px;
  object-fit: cover;
  border-radius: var(--radius);
  margin-bottom: 24px;
}
.detail-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 16px;
}
.detail-head h1 { font-family: var(--font-display); font-size: 1.75rem; }
.muted { color: var(--color-text-muted); margin-top: 8px; }
</style>
