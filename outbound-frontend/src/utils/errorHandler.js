import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import router from '@/router'

/**
 * 全局错误处理
 * 统一处理前端各类错误
 */

/**
 * HTTP错误处理
 */
export const handleHttpError = (error) => {
  const { response } = error
  
  if (response) {
    const status = response.status
    const message = response.data?.message || '请求失败'
    
    switch (status) {
      case 400:
        ElMessage.error(message || '请求参数错误')
        break
      case 401:
        handleUnauthorized()
        break
      case 403:
        ElMessage.error(message || '没有权限执行此操作')
        break
      case 404:
        ElMessage.error(message || '请求的资源不存在')
        break
      case 405:
        ElMessage.error('请求方法不允许')
        break
      case 408:
        ElMessage.error('请求超时，请稍后重试')
        break
      case 409:
        ElMessage.error(message || '资源冲突')
        break
      case 422:
        ElMessage.error(message || '数据验证失败')
        break
      case 429:
        ElMessage.error('请求过于频繁，请稍后再试')
        break
      case 500:
        ElMessage.error(message || '服务器内部错误')
        break
      case 502:
        ElMessage.error('网关错误')
        break
      case 503:
        ElMessage.error('服务不可用')
        break
      case 504:
        ElMessage.error('网关超时')
        break
      default:
        ElMessage.error(`请求失败: ${message}`)
    }
  } else {
    // 网络错误或请求未发出
    if (error.message.includes('Network Error')) {
      ElMessage.error('网络连接失败，请检查网络设置')
    } else if (error.message.includes('timeout')) {
      ElMessage.error('请求超时，请稍后重试')
    } else {
      ElMessage.error(error.message || '未知错误')
    }
  }
  
  return Promise.reject(error)
}

/**
 * 未授权处理
 */
const handleUnauthorized = () => {
  const userStore = useUserStore()
  
  ElMessageBox.confirm(
    '登录已过期，请重新登录',
    '提示',
    {
      confirmButtonText: '重新登录',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    userStore.logout()
    router.push('/login')
  })
}

/**
 * 业务错误处理
 */
export const handleBusinessError = (code, message) => {
  const errorMap = {
    1001: '用户不存在',
    1002: '用户已存在',
    1003: '用户名或密码错误',
    1004: '账号已被禁用',
    2001: '物料不存在',
    2002: '物料编码已存在',
    3001: '产品不存在',
    3002: '产品条码已存在',
    4001: '货单不存在',
    4002: '货单编号已存在',
    4003: '货单状态不允许此操作',
    5001: '客柜不存在',
    6001: '产品不在当前货单中',
    6002: '打包数量超过计划数量',
    6003: '客柜已封箱，无法打包',
    7001: '标签打印失败'
  }
  
  const errorMessage = errorMap[code] || message || '操作失败'
  ElMessage.error(errorMessage)
  
  return errorMessage
}

/**
 * 表单验证错误处理
 */
export const handleFormError = (errors) => {
  if (typeof errors === 'string') {
    ElMessage.error(errors)
    return
  }
  
  if (Array.isArray(errors)) {
    errors.forEach(error => {
      ElMessage.error(error.message || error)
    })
    return
  }
  
  if (typeof errors === 'object') {
    Object.values(errors).forEach(messages => {
      if (Array.isArray(messages)) {
        messages.forEach(msg => ElMessage.error(msg))
      } else {
        ElMessage.error(messages)
      }
    })
  }
}

/**
 * 全局错误边界（Vue错误处理）
 */
export const setupErrorHandler = (app) => {
  // Vue全局错误处理
  app.config.errorHandler = (err, vm, info) => {
    console.error('Vue Error:', err)
    console.error('Component:', vm)
    console.error('Info:', info)
    
    ElMessage.error('系统出现错误，请刷新页面重试')
  }
  
  // Promise未捕获错误
  window.addEventListener('unhandledrejection', (event) => {
    console.error('Unhandled Promise Rejection:', event.reason)
    event.preventDefault()
  })
  
  // JS运行时错误
  window.addEventListener('error', (event) => {
    console.error('Global Error:', event.error)
    
    // 资源加载错误
    if (event.target && (event.target.src || event.target.href)) {
      console.error('Resource load error:', event.target.src || event.target.href)
      ElMessage.error('资源加载失败，请检查网络')
    }
  })
}

/**
 * 重试机制
 */
export const withRetry = async (fn, maxRetries = 3, delay = 1000) => {
  let lastError
  
  for (let i = 0; i < maxRetries; i++) {
    try {
      return await fn()
    } catch (error) {
      lastError = error
      
      // 如果是401错误或用户取消，不重试
      if (error.response?.status === 401 || error.name === 'CancelError') {
        throw error
      }
      
      if (i < maxRetries - 1) {
        await new Promise(resolve => setTimeout(resolve, delay * (i + 1)))
      }
    }
  }
  
  throw lastError
}
