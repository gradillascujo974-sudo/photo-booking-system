import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getUser } from '@/utils/storage'

export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  const user = getUser()
  return axios.post('/api/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      ...(user?.id ? { 'X-User-Id': String(user.id) } : {})
    }
  }).then(res => {
    const body = res.data
    if (body.code !== 200) {
      ElMessage.error(body.msg || '上传失败')
      return Promise.reject(new Error(body.msg))
    }
    return body.data
  }).catch(err => {
    const msg = err.response?.data?.msg || err.message || '上传失败'
    ElMessage.error(msg)
    return Promise.reject(err)
  })
}
