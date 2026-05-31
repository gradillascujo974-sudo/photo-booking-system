import request from '@/utils/request'
import { ORDER_STATUS } from '@/constants'

export function getReviews() {
  return request.get('/comments')
}

export function createReview(data) {
  return request.post('/comments', data)
}

export function canReviewOrder(order) {
  return order.status === ORDER_STATUS.DONE
}
