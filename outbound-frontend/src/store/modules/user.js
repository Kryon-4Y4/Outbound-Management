import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/api/request'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const permissions = ref([])

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value.username || '')

  // Actions
  const login = async (loginForm) => {
    const data = await request.post('/auth/login', loginForm)
    token.value = data.token
    userInfo.value = data.userInfo
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(data.userInfo))
    return data
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    permissions.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const getUserInfo = async () => {
    const data = await request.get('/auth/info')
    userInfo.value = data
    permissions.value = data.permissions || []
    localStorage.setItem('userInfo', JSON.stringify(data))
    return data
  }

  return {
    token,
    userInfo,
    permissions,
    isLoggedIn,
    username,
    login,
    logout,
    getUserInfo
  }
})
