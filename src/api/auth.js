import request from '@/utils/request'
import { getUser, setUser } from '@/utils/storage'

export async function login({ username, password, role }) {
  const user = await request.post('/login', { username, password, role })
  setUser(user)
  return user
}

export async function register(data) {
  const user = await request.post('/register', data)
  setUser(user)
  return user
}

export function logout() {
  setUser(null)
}

export async function refreshUser() {
  const user = await request.get('/user/me')
  setUser(user)
  return user
}

export async function updateProfile(updates) {
  const user = await request.put('/user/profile', updates)
  setUser(user)
  return user
}

export async function recharge(amount) {
  const user = await request.post('/user/recharge', { amount })
  setUser(user)
  return user
}
