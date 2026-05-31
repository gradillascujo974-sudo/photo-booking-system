<template>
  <div class="page-container">
    <h1 class="section-title">作品集</h1>
    <p class="section-desc">与摄影师后台同一数据源，展示已上架作品</p>

    <el-input v-model="keyword" placeholder="搜索作品名称" clearable style="max-width: 320px; margin-bottom: 24px" @change="load" />

    <div v-if="list.length" class="portfolio-grid">
      <div
        v-for="p in list"
        :key="p.id"
        class="portfolio-card"
        @click="$router.push(`/portfolios/${p.id}`)"
      >
        <img :src="resolveImg(p.img)" :alt="p.name" />
        <div class="overlay">
          <h3>{{ p.name }}</h3>
          <p>{{ p.descr }}</p>
          <span class="meta">{{ p.photographerName }} · ❤ {{ likeCounts[p.id] ?? p.likeCount }}</span>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无已上架作品" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { fetchPortfolios } from '@/api/catalog'
import { getLikeCount } from '@/api/likes'
import { resolveImg } from '@/utils/img'

const route = useRoute()
const keyword = ref('')
const list = ref([])
const likeCounts = ref({})

async function load() {
  const params = { keyword: keyword.value || undefined }
  if (route.query.photographerId) {
    params.photographerId = Number(route.query.photographerId)
  }
  list.value = await fetchPortfolios(params)
  const counts = {}
  await Promise.all(list.value.map(async p => {
    try {
      counts[p.id] = await getLikeCount(p.id)
    } catch {
      counts[p.id] = p.likeCount
    }
  }))
  likeCounts.value = counts
}

onMounted(load)
</script>

<style scoped>
.portfolio-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.portfolio-card {
  position: relative;
  border-radius: var(--radius);
  overflow: hidden;
  aspect-ratio: 4/3;
  cursor: pointer;
}
.portfolio-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.portfolio-card:hover img { transform: scale(1.05); }
.overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(transparent 40%, rgba(0,0,0,0.85));
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 20px;
}
.overlay h3 { font-size: 1.1rem; }
.overlay p { font-size: 0.85rem; color: var(--color-text-muted); margin: 4px 0 8px; }
.meta { font-size: 0.8rem; color: var(--color-accent); }
</style>
