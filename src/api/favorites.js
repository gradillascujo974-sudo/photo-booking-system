import request from '@/utils/request'

export function getFavorites() {
  return request.get('/favorites')
}

export function toggleFavorite(serverId) {
  return request.post(`/favorites/toggle/${serverId}`)
}
