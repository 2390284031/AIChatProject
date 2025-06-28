import axios, { type AxiosError } from 'axios'
import { getToken, removeToken } from '@/utils/token'
import Router from '@/router'
import { userStore } from '@/stores/user'
import { message } from 'ant-design-vue'

const userstore = userStore()

const service = axios.create({
  // url = base url + request url
  baseURL: '/api',
  withCredentials: true,
  timeout: 3000
})

// 添加请求拦截器
service.interceptors.request.use(
  function (config) {
    const token = getToken()
    if (token) {
      config.headers['token'] = token
    }
    return config
  },
  (e) => Promise.reject(e)
)

// 添加响应拦截器
service.interceptors.response.use(
  function (response) {
    if (response.data.code === 0 && response.data.msg === '未登录') {
      // 返回登录页面
      console.log('用户未登录, 直接跳转至登录页面')
      removeToken()
      userstore.clearAdminInfo()
      Router.replace('/login')
      return response
    } else {
      return response
    }
  },
  (error: AxiosError) => {
    console.log('err' + error)
    let msg = error.message
    if (msg === 'Network Error') {
      msg = '后端接口连接异常'
    } else if (msg.includes('timeout')) {
      msg = '系统接口请求超时'
    } else if (msg.includes('Request failed with status code')) {
      msg = '系统接口' + msg.substr(msg.length - 3) + '异常'
    }
    message.error(msg)
    return Promise.reject(error)
  }
)

export default service
