import request from '@/utils/request'

export function getIdentifies() {
  return request.get('/identify')
}

export function getMyIdentify() {
  return request.get('/identify/mine')
}

export function submitIdentify(data) {
  return request.post('/identify', data)
}

export function auditIdentify(id, status, reason) {
  return request.put(`/identify/${id}/audit`, { status, reason })
}
