import request from '@/utils/request'
import { setUser } from '@/utils/storage'

export function getPhotographerProfile() {
  return request.get('/photographer/profile')
}

export async function updatePhotographerProfile(data) {
  const profile = await request.put('/photographer/profile', data)
  const user = await request.get('/user/me')
  setUser(user)
  return profile
}
