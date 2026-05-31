import request from '@/utils/request'

export function checkLike(portfolioId) {
  return request.get(`/likes/check/${portfolioId}`)
}

export function getLikeCount(portfolioId) {
  return request.get(`/likes/count/${portfolioId}`)
}

export function toggleLike(portfolioId) {
  return request.post(`/likes/toggle/${portfolioId}`)
}

export async function isLiked(portfolioId) {
  const data = await checkLike(portfolioId)
  return data.liked
}
