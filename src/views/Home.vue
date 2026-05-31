<template>
  <div class="home">
    <section class="hero">
      <el-carousel height="420px" :interval="5000">
        <el-carousel-item v-for="b in banners" :key="b.id">
          <div class="hero-slide" :style="{ backgroundImage: `url(${b.img})` }">
            <div class="hero-overlay">
              <h1>发现你的专属摄影师</h1>
              <p>婚礼 · 写真 · 旅拍 · 亲子 · 商业 — 一站式在线约拍</p>
              <el-button type="primary" size="large" @click="$router.push('/services')">立即预约</el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <section class="page-container">
      <h2 class="section-title">热门分类</h2>
      <p class="section-desc">按场景筛选，快速找到心仪服务</p>
      <div class="category-grid">
        <div
          v-for="c in categories"
          :key="c.id"
          class="category-card"
          @click="$router.push({ path: '/services', query: { type: c.id } })"
        >
          <span>{{ c.name }}</span>
        </div>
      </div>
    </section>

    <section class="page-container">
      <div class="section-head">
        <div>
          <h2 class="section-title">精选约拍服务</h2>
          <p class="section-desc">支持风格、价格、档期多维度筛选</p>
        </div>
        <router-link to="/services">查看全部 →</router-link>
      </div>
      <div class="service-grid">
        <div v-for="s in topServices" :key="s.id" class="service-card" @click="$router.push(`/services/${s.id}`)">
          <img :src="s.img" :alt="s.name" />
          <div class="service-info">
            <h3>{{ s.name }}</h3>
            <p>{{ s.descr }}</p>
            <div class="service-meta">
              <span class="price">¥{{ s.price }}</span>
              <el-tag size="small">{{ s.style }}</el-tag>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="page-container">
      <h2 class="section-title">系统公告</h2>
      <el-timeline>
        <el-timeline-item v-for="n in notices" :key="n.id" :timestamp="n.time" placement="top">
          <el-card shadow="never"><h4>{{ n.title }}</h4><p>{{ n.content }}</p></el-card>
        </el-timeline-item>
      </el-timeline>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchBanners, fetchCategories, fetchServices, fetchNotices } from '@/api/catalog'

const banners = ref([])
const categories = ref([])
const topServices = ref([])
const notices = ref([])

onMounted(async () => {
  const [b, c, s, n] = await Promise.all([
    fetchBanners(),
    fetchCategories(),
    fetchServices(),
    fetchNotices()
  ])
  banners.value = b
  categories.value = c
  topServices.value = s.slice(0, 4)
  notices.value = n
})
</script>

<style scoped>
.hero-slide {
  height: 420px;
  background-size: cover;
  background-position: center;
  position: relative;
}
.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.75), transparent 50%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 48px 64px;
}
.hero-overlay h1 {
  font-family: var(--font-display);
  font-size: 2.5rem;
  margin-bottom: 8px;
}
.hero-overlay p {
  color: var(--color-text-muted);
  margin-bottom: 20px;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}
.category-card {
  padding: 24px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  text-align: center;
  cursor: pointer;
  transition: border-color 0.2s, transform 0.2s;
}
.category-card:hover {
  border-color: var(--color-accent);
  transform: translateY(-2px);
}
.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 8px;
}
.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
.service-card {
  background: var(--color-bg-card);
  border-radius: var(--radius);
  overflow: hidden;
  border: 1px solid var(--color-border);
  cursor: pointer;
  transition: transform 0.2s;
}
.service-card:hover { transform: translateY(-4px); }
.service-card img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}
.service-info { padding: 16px; }
.service-info h3 { font-size: 1rem; margin-bottom: 6px; }
.service-info p {
  font-size: 0.85rem;
  color: var(--color-text-muted);
  margin-bottom: 12px;
}
.service-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price { color: var(--color-accent); font-weight: 600; font-size: 1.1rem; }
</style>
