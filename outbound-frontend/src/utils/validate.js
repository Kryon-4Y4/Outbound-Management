/**
 * 表单验证规则库
 * 提供常用的表单验证规则
 */

/**
 * 必填验证
 */
export const required = (message = '此项为必填项') => ({
  required: true,
  message,
  trigger: ['blur', 'change']
})

/**
 * 字符串长度验证
 */
export const length = (min, max, message) => ({
  min,
  max,
  message: message || `长度必须在 ${min} 到 ${max} 个字符之间`,
  trigger: 'blur'
})

/**
 * 邮箱验证
 */
export const email = (message = '请输入正确的邮箱地址') => ({
  type: 'email',
  message,
  trigger: 'blur'
})

/**
 * 手机号验证
 */
export const phone = (message = '请输入正确的手机号') => ({
  pattern: /^1[3-9]\d{9}$/,
  message,
  trigger: 'blur'
})

/**
 * 电话号码验证（固话）
 */
export const tel = (message = '请输入正确的电话号码') => ({
  pattern: /^0\d{2,3}-?\d{7,8}$/,
  message,
  trigger: 'blur'
})

/**
 * 数字验证
 */
export const number = (message = '请输入数字') => ({
  pattern: /^-?\d+\.?\d*$/,
  message,
  trigger: 'blur'
})

/**
 * 正整数验证
 */
export const positiveInt = (message = '请输入正整数') => ({
  pattern: /^[1-9]\d*$/,
  message,
  trigger: 'blur'
})

/**
 * 金额验证
 */
export const money = (message = '请输入正确的金额') => ({
  pattern: /^-?\d+(\.\d{1,2})?$/,
  message,
  trigger: 'blur'
})

/**
 * 中文验证
 */
export const chinese = (message = '请输入中文') => ({
  pattern: /^[\u4e00-\u9fa5]+$/,
  message,
  trigger: 'blur'
})

/**
 * 英文验证
 */
export const english = (message = '请输入英文字母') => ({
  pattern: /^[a-zA-Z]+$/,
  message,
  trigger: 'blur'
})

/**
 * 字母数字验证
 */
export const alphanumeric = (message = '只能输入字母和数字') => ({
  pattern: /^[a-zA-Z0-9]+$/,
  message,
  trigger: 'blur'
})

/**
 * 物料编码验证（字母数字下划线）
 */
export const materialCode = (message = '物料编码只能包含字母、数字和下划线') => ({
  pattern: /^[a-zA-Z0-9_]+$/,
  message,
  trigger: 'blur'
})

/**
 * 条码验证
 */
export const barcode = (message = '请输入正确的条码') => ({
  pattern: /^[a-zA-Z0-9\-_]+$/,
  message,
  trigger: 'blur'
})

/**
 * URL验证
 */
export const url = (message = '请输入正确的URL') => ({
  pattern: /^https?:\/\/.+/,
  message,
  trigger: 'blur'
})

/**
 * 身份证号验证
 */
export const idCard = (message = '请输入正确的身份证号') => ({
  validator: (rule, value, callback) => {
    if (!value) {
      callback()
      return
    }
    
    const pattern = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
    if (!pattern.test(value)) {
      callback(new Error(message))
      return
    }
    
    // 简单的校验码验证
    if (value.length === 18) {
      const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
      const checkCodes = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']
      let sum = 0
      for (let i = 0; i < 17; i++) {
        sum += parseInt(value[i]) * weights[i]
      }
      const checkCode = checkCodes[sum % 11]
      if (checkCode !== value[17].toUpperCase()) {
        callback(new Error(message))
        return
      }
    }
    
    callback()
  },
  trigger: 'blur'
})

/**
 * 自定义正则验证
 */
export const pattern = (regexp, message) => ({
  pattern: regexp,
  message,
  trigger: 'blur'
})

/**
 * 自定义验证器
 */
export const validator = (fn, trigger = 'blur') => ({
  validator: fn,
  trigger
})

/**
 * 范围验证（数字）
 */
export const range = (min, max, message) => ({
  type: 'number',
  min,
  max,
  message: message || `数值范围必须在 ${min} 到 ${max} 之间`,
  trigger: 'blur'
})

/**
 * 数组长度验证
 */
export const arrayLength = (min, max, message) => ({
  type: 'array',
  min,
  max,
  message: message || `至少选择 ${min} 项，最多选择 ${max} 项`,
  trigger: 'change'
})

/**
 * 两次输入一致性验证
 */
export const confirm = (targetField, message = '两次输入不一致') => ({
  validator: (rule, value, callback, source) => {
    if (value !== source[targetField]) {
      callback(new Error(message))
    } else {
      callback()
    }
  },
  trigger: 'blur'
})

/**
 * 常用表单验证规则组合
 */
export const rules = {
  // 用户名：必填，3-20位字母数字下划线
  username: [
    required('请输入用户名'),
    length(3, 20, '用户名长度必须在3-20位之间'),
    alphanumeric('用户名只能包含字母、数字')
  ],
  
  // 密码：必填，6-20位
  password: [
    required('请输入密码'),
    length(6, 20, '密码长度必须在6-20位之间')
  ],
  
  // 确认密码
  confirmPassword: (passwordField) => [
    required('请确认密码'),
    confirm(passwordField, '两次输入的密码不一致')
  ],
  
  // 手机号
  phone: [
    required('请输入手机号'),
    phone()
  ],
  
  // 邮箱
  email: [
    required('请输入邮箱'),
    email()
  ],
  
  // 物料编码
  materialCode: [
    required('请输入物料编码'),
    length(1, 50, '物料编码长度不能超过50位'),
    materialCode()
  ],
  
  // 物料名称
  materialName: [
    required('请输入物料名称'),
    length(1, 100, '物料名称长度不能超过100位')
  ],
  
  // 产品条码
  productBarcode: [
    required('请输入产品条码'),
    length(1, 50, '条码长度不能超过50位'),
    barcode()
  ],
  
  // 产品名称
  productName: [
    required('请输入产品名称'),
    length(1, 100, '产品名称长度不能超过100位')
  ],
  
  // 数量
  quantity: [
    required('请输入数量'),
    number('请输入正确的数字'),
    { type: 'number', min: 1, message: '数量必须大于0', trigger: 'blur' }
  ],
  
  // 金额
  amount: [
    required('请输入金额'),
    money()
  ]
}

export default rules
