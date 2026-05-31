export const ROLES = {
  USER: 'USER',
  PHOTOGRAPHER: 'PHOTOGRAPHER',
  ADMIN: 'ADMIN'
}

export const ROLE_LABELS = {
  USER: '普通用户',
  PHOTOGRAPHER: '摄影师',
  ADMIN: '管理员'
}

export const ORDER_STATUS = {
  PENDING: '待接单',
  READY: '待开始',
  DONE: '已完成',
  CANCELLED: '已取消'
}

export const users = [
  { id: 1, username: 'user123', password: '123456', role: 'USER', name: '张小雅', phone: '13800138001', email: 'user@demo.com', account: 2000, avatar: '' },
  { id: 2, username: 'photo01', password: '123456', role: 'PHOTOGRAPHER', name: '李镜头', phone: '13900139001', email: 'photo@demo.com', account: 5800, avatar: '', introduce: '8年婚礼与人像经验，擅长自然光与纪实风格。' },
  { id: 3, username: 'admin', password: 'admin123', role: 'ADMIN', name: '系统管理员', phone: '13700137001', email: 'admin@demo.com', avatar: '' }
]

export const categories = [
  { id: 1, name: '婚礼摄影' },
  { id: 2, name: '个人写真' },
  { id: 3, name: '亲子纪实' },
  { id: 4, name: '旅拍跟拍' },
  { id: 5, name: '商业拍摄' }
]

export const photographers = [
  { id: 2, name: '李镜头', username: 'photo01', avatar: '', introduce: '8年婚礼与人像经验，擅长自然光与纪实风格。', certified: true, rating: 4.9, city: '景德镇', styles: ['婚礼', '纪实', '人像'] },
  { id: 4, name: '陈光影', username: 'photo02', avatar: '', introduce: '日系清新风格摄影师，旅拍经验丰富。', certified: true, rating: 4.8, city: '南昌', styles: ['日系', '旅拍', '写真'] },
  { id: 5, name: '王快门', username: 'photo03', avatar: '', introduce: '商业摄影与证件照专家。', certified: false, rating: 4.6, city: '九江', styles: ['商业', '证件照'] }
]

export const services = [
  { id: 1, name: '婚礼全天跟拍', descr: '从化妆到晚宴全程记录', content: '含精修50张，原片全送', img: 'https://images.unsplash.com/photo-1519741497674-611481863552?w=800&q=80', price: 3888, style: '婚礼', tags: '跟拍,纪实', typeId: 1, photographerId: 2, readCount: 1280, saleCount: 86, status: '上架' },
  { id: 2, name: '个人写真套餐', descr: '2小时棚拍或外景', content: '含妆造建议与15张精修', img: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&q=80', price: 699, style: '人像', tags: '写真,棚拍', typeId: 2, photographerId: 2, readCount: 920, saleCount: 142, status: '上架' },
  { id: 3, name: '日系旅拍半日', descr: '城市漫步式拍摄', content: '适合情侣与闺蜜', img: 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?w=800&q=80', price: 1288, style: '日系', tags: '旅拍,清新', typeId: 4, photographerId: 4, readCount: 756, saleCount: 58, status: '上架' },
  { id: 4, name: '亲子纪实跟拍', descr: '记录成长瞬间', content: '不上妆，自然抓拍', img: 'https://images.unsplash.com/photo-1511895426328-acd190e3a8ba?w=800&q=80', price: 899, style: '纪实', tags: '亲子,家庭', typeId: 3, photographerId: 2, readCount: 445, saleCount: 33, status: '上架' },
  { id: 5, name: '商业产品拍摄', descr: '电商主图与详情页', content: '按件计费，可议价', img: 'https://images.unsplash.com/photo-1542038784456-1ea8e935640e?w=800&q=80', price: 500, style: '商业', tags: '产品,电商', typeId: 5, photographerId: 5, readCount: 312, saleCount: 21, status: '上架' }
]

export const portfolios = [
  { id: 1, name: '秋日婚礼纪实', descr: '景德镇户外婚礼', img: 'https://images.unsplash.com/photo-1606216794074-735e91aa2a92?w=600&q=80', photographerId: 2, readCount: 2340, likeCount: 186 },
  { id: 2, name: '清新人像集', descr: '自然光棚拍作品', img: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=600&q=80', photographerId: 2, readCount: 1890, likeCount: 245 },
  { id: 3, name: '京都旅拍风格', descr: '日系街道与和服', img: 'https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=600&q=80', photographerId: 4, readCount: 1560, likeCount: 198 },
  { id: 4, name: '亲子日常', descr: '家庭纪实摄影', img: 'https://images.unsplash.com/photo-1476703993599-003027a8e0a2?w=600&q=80', photographerId: 2, readCount: 980, likeCount: 87 }
]

export const banners = [
  { id: 1, img: 'https://images.unsplash.com/photo-1511285560929-80b456fea0bc?w=1400&q=80', serverId: 1 },
  { id: 2, img: 'https://images.unsplash.com/photo-1522673607210-f26f0ebdca72?w=1400&q=80', serverId: 2 },
  { id: 3, img: 'https://images.unsplash.com/photo-1465495976277-438e610ebb0f?w=1400&q=80', serverId: 3 }
]

export const notices = [
  { id: 1, title: '平台上线公告', content: '光影约拍系统正式上线，欢迎摄影师入驻认证。', time: '2026-05-01' },
  { id: 2, title: '五一档期提醒', content: '热门摄影师档期紧张，请提前预约。', time: '2026-04-28' }
]

export function getPhotographerById(id) {
  return photographers.find(p => p.id === id)
}

export function getServiceById(id) {
  return services.find(s => s.id === id)
}

export function getPortfolioById(id) {
  return portfolios.find(p => p.id === id)
}
