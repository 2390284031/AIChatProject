import request from '@/utils/request.ts'

export function getHots() {
  return request({
    url: "/chat/getHots",
    method: 'get'
  })
}
