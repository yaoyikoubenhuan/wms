import { defineStore } from 'pinia'
import { authApi } from '@/api'
import { TOKEN_KEY } from '@/api/request'

const USER_KEY = 'wms_user'

function loadUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    return null
  }
}

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const currentUser = ref(loadUser())

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  async function login(loginForm) {
    const data = await authApi.login(loginForm)
    token.value = data.token
    currentUser.value = data.user
    localStorage.setItem(TOKEN_KEY, data.token)
    localStorage.setItem(USER_KEY, JSON.stringify(data.user))
    return data.user
  }

  async function logout() {
    try {
      if (token.value) await authApi.logout()
    } finally {
      token.value = ''
      currentUser.value = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
  }

  return { sidebarCollapsed, token, currentUser, toggleSidebar, login, logout }
})
