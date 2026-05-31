<template>
  <div class="page-container">
    <h1 class="section-title">摄影师</h1>
    <p class="section-desc">按风格、城市筛选，查看认证状态与评分</p>

    <el-card shadow="never" class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索姓名/风格" clearable style="max-width: 280px" @change="load" />
      <el-select v-model="city" clearable placeholder="城市" style="width: 120px; margin-left: 12px" @change="load">
        <el-option v-for="c in cities" :key="c" :label="c" :value="c" />
      </el-select>
    </el-card>

    <div class="photo-grid">
      <el-card v-for="p in list" :key="p.id" shadow="hover" class="photo-card">
        <div class="photo-head">
          <el-avatar :size="64">{{ p.name?.[0] }}</el-avatar>
          <div>
            <h3>{{ p.name }}</h3>
            <el-rate :model-value="Number(p.rating)" disabled show-score text-color="#c9a962" />
          </div>
          <el-tag v-if="p.certified" type="success">已认证</el-tag>
        </div>
        <p class="intro">{{ p.introduce }}</p>
        <div class="styles">
          <el-tag v-for="s in (p.styles || [])" :key="s" size="small">{{ s }}</el-tag>
        </div>
        <p class="city">📍 {{ p.city }}</p>
        <el-button type="primary" text @click="$router.push({ path: '/portfolios', query: { photographerId: p.id } })">
          查看作品集
        </el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchPhotographers } from '@/api/catalog'

const keyword = ref('')
const city = ref('')
const list = ref([])
const cities = ref([])

async function load() {
  list.value = await fetchPhotographers({
    keyword: keyword.value || undefined,
    city: city.value || undefined
  })
  cities.value = [...new Set(list.value.map(p => p.city).filter(Boolean))]
}

onMounted(load)
</script>

<style scoped>
.filter-bar { margin-bottom: 24px; display: flex; align-items: center; flex-wrap: wrap; gap: 8px; }
.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}
.photo-head {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}
.photo-head h3 { flex: 1; }
.intro { color: var(--color-text-muted); font-size: 0.9rem; margin-bottom: 12px; line-height: 1.5; }
.styles { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 8px; }
.city { font-size: 0.85rem; color: var(--color-text-muted); margin-bottom: 12px; }
</style>
