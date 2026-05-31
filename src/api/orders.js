import request from '@/utils/request'
import { refreshUser } from '@/api/auth'

export function getOrders() {
  return request.get('/orders')
}

export async function createOrder(data) {
  const order = await request.post('/orders', data)
  return order
}

export async function contactOrder(orderId) {
  const order = await request.post(`/orders/${orderId}/contact`)
  await refreshUser()
  return order
}

export async function confirmOrder(orderId) {
  const data = await request.post(`/orders/${orderId}/confirm`)
  await refreshUser()
  return data
}

export function getOrderContact(orderId) {
  return request.get(`/orders/${orderId}/contact`)
}

export function updateOrderStatus(orderId, status) {
  return request.put(`/orders/${orderId}/status`, { status })
}
