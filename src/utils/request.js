import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getUser } from '@/utils/storage'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const user = getUser()
  if (user?.id) {
    config.headers['X-User-Id'] = String(user.id)
  }
  return config
})

request.interceptors.response.use(
  res => {
    const body = res.data
    if (body.code !== 200) {
      ElMessage.error(body.msg || '请求失败')
      return Promise.reject(new Error(body.msg || '请求失败'))
    }
    return body.data
  },
  err => {
    const msg = err.response?.data?.msg || err.message || '网络错误'
    ElMessage.error(msg)
    return Promise.reject(err)
  }
)

export default request
