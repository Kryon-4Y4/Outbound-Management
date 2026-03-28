<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    :label-width="labelWidth"
    :label-position="labelPosition"
    class="pro-form"
    v-bind="$attrs"
  >
    <el-row :gutter="gutter">
      <el-col
        v-for="item in columns"
        :key="item.prop"
        :span="item.span || 24"
      >
        <el-form-item
          :label="item.label"
          :prop="item.prop"
          :rules="item.rules"
        >
          <!-- 输入框 -->
          <template v-if="item.type === 'input' || !item.type">
            <el-input
              v-model="formData[item.prop]"
              :placeholder="item.placeholder || `请输入${item.label}`"
              :disabled="item.disabled"
              :readonly="item.readonly"
              :clearable="item.clearable !== false"
              :show-password="item.showPassword"
              :maxlength="item.maxlength"
              :rows="item.rows"
              :type="item.inputType || 'text'"
            >
              <template v-if="item.prefix" #prefix>
                <component :is="item.prefix" />
              </template>
              <template v-if="item.suffix" #suffix>
                <component :is="item.suffix" />
              </template>
            </el-input>
          </template>
          
          <!-- 数字输入框 -->
          <template v-else-if="item.type === 'number'">
            <el-input-number
              v-model="formData[item.prop]"
              :placeholder="item.placeholder"
              :disabled="item.disabled"
              :min="item.min"
              :max="item.max"
              :step="item.step || 1"
              :precision="item.precision"
              :controls="item.controls !== false"
              style="width: 100%"
            />
          </template>
          
          <!-- 文本域 -->
          <template v-else-if="item.type === 'textarea'">
            <el-input
              v-model="formData[item.prop]"
              type="textarea"
              :placeholder="item.placeholder || `请输入${item.label}`"
              :disabled="item.disabled"
              :readonly="item.readonly"
              :rows="item.rows || 3"
              :maxlength="item.maxlength"
              show-word-limit
            />
          </template>
          
          <!-- 选择框 -->
          <template v-else-if="item.type === 'select'">
            <el-select
              v-model="formData[item.prop]"
              :placeholder="item.placeholder || `请选择${item.label}`"
              :disabled="item.disabled"
              :clearable="item.clearable !== false"
              :multiple="item.multiple"
              :filterable="item.filterable"
              style="width: 100%"
            >
              <el-option
                v-for="opt in item.options"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
          </template>
          
          <!-- 级联选择 -->
          <template v-else-if="item.type === 'cascader'">
            <el-cascader
              v-model="formData[item.prop]"
              :options="item.options"
              :placeholder="item.placeholder || `请选择${item.label}`"
              :disabled="item.disabled"
              :clearable="item.clearable !== false"
              :props="item.props"
              style="width: 100%"
            />
          </template>
          
          <!-- 日期选择 -->
          <template v-else-if="item.type === 'date'">
            <el-date-picker
              v-model="formData[item.prop]"
              type="date"
              :placeholder="item.placeholder || `请选择${item.label}`"
              :disabled="item.disabled"
              :clearable="item.clearable !== false"
              :value-format="item.valueFormat || 'YYYY-MM-DD'"
              style="width: 100%"
            />
          </template>
          
          <!-- 日期时间选择 -->
          <template v-else-if="item.type === 'datetime'">
            <el-date-picker
              v-model="formData[item.prop]"
              type="datetime"
              :placeholder="item.placeholder || `请选择${item.label}`"
              :disabled="item.disabled"
              :clearable="item.clearable !== false"
              :value-format="item.valueFormat || 'YYYY-MM-DD HH:mm:ss'"
              style="width: 100%"
            />
          </template>
          
          <!-- 单选框 -->
          <template v-else-if="item.type === 'radio'">
            <el-radio-group v-model="formData[item.prop]" :disabled="item.disabled">
              <el-radio
                v-for="opt in item.options"
                :key="opt.value"
                :label="opt.value"
              >
                {{ opt.label }}
              </el-radio>
            </el-radio-group>
          </template>
          
          <!-- 复选框 -->
          <template v-else-if="item.type === 'checkbox'">
            <el-checkbox-group v-model="formData[item.prop]" :disabled="item.disabled">
              <el-checkbox
                v-for="opt in item.options"
                :key="opt.value"
                :label="opt.value"
              >
                {{ opt.label }}
              </el-checkbox>
            </el-checkbox-group>
          </template>
          
          <!-- 开关 -->
          <template v-else-if="item.type === 'switch'">
            <el-switch
              v-model="formData[item.prop]"
              :disabled="item.disabled"
              :active-value="item.activeValue !== undefined ? item.activeValue : 1"
              :inactive-value="item.inactiveValue !== undefined ? item.inactiveValue : 0"
            />
          </template>
          
          <!-- 文件上传 -->
          <template v-else-if="item.type === 'upload'">
            <el-upload
              v-model:file-list="formData[item.prop]"
              :action="item.action"
              :headers="item.headers"
              :multiple="item.multiple"
              :limit="item.limit"
              :accept="item.accept"
              :before-upload="item.beforeUpload"
              :on-success="item.onSuccess"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon>点击上传
              </el-button>
              <template #tip v-if="item.tip">
                <div class="el-upload__tip">{{ item.tip }}</div>
              </template>
            </el-upload>
          </template>
          
          <!-- 自定义插槽 -->
          <template v-else-if="item.type === 'slot'">
            <slot :name="item.prop" :form="formData" />
          </template>
        </el-form-item>
      </el-col>
    </el-row>
    
    <!-- 操作按钮 -->
    <el-form-item v-if="showActions" class="form-actions">
      <slot name="actions" :submit="submit" :reset="reset" :form="formData">
        <el-button type="primary" :loading="submitting" @click="submit">
          {{ submitText }}
        </el-button>
        <el-button @click="reset">{{ resetText }}</el-button>
        <el-button v-if="showCancel" @click="cancel">{{ cancelText }}</el-button>
      </slot>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { Upload } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  // 表单列配置
  columns: {
    type: Array,
    required: true
  },
  // 初始数据
  initialData: {
    type: Object,
    default: () => ({})
  },
  // 提交函数
  submitHandler: {
    type: Function,
    default: null
  },
  // 验证规则
  rules: {
    type: Object,
    default: () => ({})
  },
  // 标签宽度
  labelWidth: {
    type: String,
    default: '100px'
  },
  // 标签位置
  labelPosition: {
    type: String,
    default: 'right'
  },
  // 栅格间距
  gutter: {
    type: Number,
    default: 20
  },
  // 是否显示操作按钮
  showActions: {
    type: Boolean,
    default: true
  },
  // 提交按钮文字
  submitText: {
    type: String,
    default: '提交'
  },
  // 重置按钮文字
  resetText: {
    type: String,
    default: '重置'
  },
  // 是否显示取消按钮
  showCancel: {
    type: Boolean,
    default: false
  },
  // 取消按钮文字
  cancelText: {
    type: String,
    default: '取消'
  }
})

const emit = defineEmits(['submit', 'reset', 'cancel', 'success'])

const formRef = ref(null)
const formData = reactive({})
const formRules = reactive({ ...props.rules })
const submitting = ref(false)

// 初始化表单数据
const initFormData = () => {
  props.columns.forEach(item => {
    if (item.prop) {
      formData[item.prop] = props.initialData[item.prop] !== undefined 
        ? props.initialData[item.prop] 
        : item.defaultValue
    }
  })
}

// 监听initialData变化
watch(() => props.initialData, (newVal) => {
  Object.assign(formData, newVal)
}, { deep: true })

// 提交
const submit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (props.submitHandler) {
      submitting.value = true
      await props.submitHandler(formData)
      ElMessage.success('操作成功')
      emit('success')
    }
    
    emit('submit', formData)
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

// 重置
const reset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
    initFormData()
  }
  emit('reset')
}

// 取消
const cancel = () => {
  emit('cancel')
}

// 验证单个字段
const validateField = (field) => {
  return formRef.value?.validateField(field)
}

// 清除验证
const clearValidate = () => {
  formRef.value?.clearValidate()
}

// 设置表单值
const setFieldsValue = (values) => {
  Object.assign(formData, values)
}

// 获取表单值
const getFieldsValue = () => {
  return { ...formData }
}

onMounted(() => {
  initFormData()
})

defineExpose({
  submit,
  reset,
  validate: () => formRef.value?.validate(),
  validateField,
  clearValidate,
  setFieldsValue,
  getFieldsValue,
  formRef
})
</script>

<style scoped>
.pro-form {
  .form-actions {
    margin-top: 20px;
  }
}
</style>
