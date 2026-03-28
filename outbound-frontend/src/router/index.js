import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/system/login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/components/common/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/system/dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      // 用户管理
      {
        path: '/user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'UserFilled' }
      },
      // 物料管理
      {
        path: '/material',
        name: 'Material',
        component: () => import('@/views/material/index.vue'),
        meta: { title: '物料管理', icon: 'Box' }
      },
      // 产品管理
      {
        path: '/product',
        name: 'Product',
        component: () => import('@/views/product/index.vue'),
        meta: { title: '产品管理', icon: 'GoodsFilled' }
      },
      // 货单管理
      {
        path: '/order',
        name: 'Order',
        component: () => import('@/views/order/index.vue'),
        meta: { title: '货单管理', icon: 'Document' }
      },
      // 打包作业
      {
        path: '/packing',
        name: 'Packing',
        component: () => import('@/views/packing/index.vue'),
        meta: { title: '产品打包', icon: 'ShoppingCartFull' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/system/404.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  NProgress.start()
  
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title + ' - WMS仓库管理系统'
  }
  
  // 公开页面直接访问
  if (to.meta.public) {
    next()
    return
  }
  
  // 检查登录状态
  if (!userStore.token) {
    next('/login')
    return
  }
  
  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router
