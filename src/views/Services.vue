<template>
  <div class="page-container">
    <h1 class="section-title">约拍服务</h1>
    <p class="section-desc">多条件筛选：风格、价格、分类</p>

    <el-card class="filter-bar" shadow="never">
      <el-form :inline="true">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="服务名称/标签" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="风格">
          <el-select v-model="filters.style" clearable placeholder="全部" style="width: 120px">
            <el-option v-for="s in styles" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-select v-model="filters.priceRange" clearable placeholder="全部" style="width: 140px">
            <el-option label="0-500元" value="0-500" />
            <el-option label="500-1500元" value="500-1500" />
            <el-option label="1500元以上" value="1500+" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filters.typeId" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-button type="primary" @click="load">筛选</el-button>
      </el-form>
    </el-card>

    <div v-if="list.length" class="service-grid">
      <div v-for="s in list" :key="s.id" class="service-card" @click="$router.push(`/services/${s.id}`)">
        <img :src="s.img" :alt="s.name" />
        <div class="service-info">
          <h3>{{ s.name }}</h3>
          <p>{{ s.descr }}</p>
          <div class="service-meta">
            <span class="price">¥{{ s.price }}</span>
            <span class="muted">已售 {{ s.saleCount }}</span>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无相关服务" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { fetchServices, fetchCategories } from '@/api/catalog'

const route = useRoute()
const list = ref([])
const categories = ref([])
const styles = ref([])
const filters = ref({ keyword: '', style: '', priceRange: '', typeId: null })

async function load() {
  const params = {
    keyword: filters.value.keyword || undefined,
    style: filters.value.style || undefined,
    typeId: filters.value.typeId || undefined
  }
  if (filters.value.priceRange === '0-500') {
    params.priceMin = 0
    params.priceMax = 500
  } else if (filters.value.priceRange === '500-1500') {
    params.priceMin = 500
    params.priceMax = 1500
  } else if (filters.value.priceRange === '1500+') {
    params.priceMin = 1500
  }
  list.value = await fetchServices(params)
  const s = new Set(list.value.map(i => i.style))
  styles.value = [...s]
}

onMounted(async () => {
  categories.value = await fetchCategories()
  if (route.query.type) filters.value.typeId = Number(route.query.type)
  await load()
})
</script>

<style scoped>
.filter-bar { margin-bottom: 24px; }
.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}
.service-card {
  background: var(--color-bg-card);
  border-radius: var(--radius);
  overflow: hidden;
  border: 1px solid var(--color-border);
  cursor: pointer;
}
.service-card img { width: 100%; height: 200px; object-fit: cover; }
.service-info { padding: 16px; }
.service-info h3 { margin-bottom: 6px; }
.service-info p { font-size: 0.85rem; color: var(--color-text-muted); }
.service-meta { display: flex; justify-content: space-between; margin-top: 12px; }
.price { color: var(--color-accent); font-weight: 600; }
.muted { color: var(--color-text-muted); font-size: 0.85rem; }
</style>
