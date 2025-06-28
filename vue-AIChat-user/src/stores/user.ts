// 管理用户数据
import { defineStore } from 'pinia'
import { ref } from 'vue'

// Pinia持久化管理用户信息
export const userStore = defineStore(
  'user',
  () => {
    // 定义管理用户的数据state
    const adminInfo = ref({})
    // 清除用户数据
    const clearAdminInfo = () => {
      adminInfo.value = {}
    }

    // 以对象格式将state与action return
    return {
        adminInfo,
      clearAdminInfo,
    }
  },
  {
    persist: true
  }
)
