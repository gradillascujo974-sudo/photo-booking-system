import request from '@/utils/request'

export function updateUserPoints(userId, points) {
  return request.put(`/admin/users/${userId}/points`, { points })
}

export function createAdminNotice(data) {
  return request.post('/admin/notices', data)
}

export function updateAdminNotice(id, data) {
  return request.put(`/admin/notices/${id}`, data)
}

export function deleteAdminNotice(id) {
  return request.delete(`/admin/notices/${id}`)
}
