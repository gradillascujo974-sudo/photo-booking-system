import request from '@/utils/request'

export function createPortfolio(data) {
  return request.post('/photographer/portfolios', data)
}

export function updatePortfolio(id, data) {
  return request.put(`/photographer/portfolios/${id}`, data)
}

export function deletePortfolio(id) {
  return request.delete(`/photographer/portfolios/${id}`)
}

export function fetchAdminPortfolios(params) {
  return request.get('/admin/portfolios', { params })
}

export function adminCreatePortfolio(data) {
  return request.post('/admin/portfolios', data)
}

export function adminUpdatePortfolio(id, data) {
  return request.put(`/admin/portfolios/${id}`, data)
}

export function adminDeletePortfolio(id) {
  return request.delete(`/admin/portfolios/${id}`)
}
