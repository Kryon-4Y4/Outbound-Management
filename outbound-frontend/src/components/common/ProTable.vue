<template>
  <div class="pro-table">
    <!-- 搜索表单 -->
    <el-form
      v-if="searchable"
      :model="searchForm"
      inline
      class="search-form"
      @keyup.enter="handleSearch"
    >
      <slot name="search-items" :form="searchForm">
        <el-form-item
          v-for="item in searchColumns"
          :key="item.prop"
          :label="item.label"
        >
          <!-- 输入框 -->
          <el-input
            v-if="item.type === 'input' || !item.type"
            v-model="searchForm[item.prop]"
            :placeholder="item.placeholder || `请输入${item.label}`"
            clearable
          />
          
          <!-- 选择框 -->
          <el-select
            v-else-if="item.type === 'select'"
            v-model="searchForm[item.prop]"
            :placeholder="item.placeholder || `请选择${item.label}`"
            clearable
          >
            <el-option
              v-for="opt in item.options"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
          
          <!-- 日期范围 -->
          <el-date-picker
            v-else-if="item.type === 'daterange'"
            v-model="searchForm[item.prop]"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </slot>
      
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </el-form-item>
    </el-form>
    
    <!-- 工具栏 -->
    <div class="toolbar" v-if="showToolbar">
      <slot name="toolbar" />
    </div>
    
    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      highlight-current-row
      @selection-change="handleSelectionChange"
      v-bind="$attrs"
    >
      <!-- 选择列 -->
      <el-table-column
        v-if="selection"
        type="selection"
        width="55"
        align="center"
      />
      
      <!-- 序号列 -->
      <el-table-column
        v-if="showIndex"
        type="index"
        label="序号"
        width="60"
        align="center"
        :index="(index) => (currentPage - 1) * pageSize + index + 1"
      />
      
      <!-- 数据列 -->
      <el-table-column
        v-for="column in tableColumns"
        :key="column.prop"
        :prop="column.prop"
        :label="column.label"
        :width="column.width"
        :min-width="column.minWidth"
        :align="column.align || 'left'"
        :fixed="column.fixed"
        :sortable="column.sortable"
        show-overflow-tooltip
      >
        <template #default="{ row, $index }">
          <!-- 自定义插槽 -->
          <slot
            v-if="column.slot"
            :name="column.prop"
            :row="row"
            :index="$index"
          />
          
          <!-- 枚举值映射 -->
          <template v-else-if="column.enum">
            <el-tag :type="getEnumTagType(column.enum, row[column.prop])">
              {{ getEnumLabel(column.enum, row[column.prop]) }}
            </el-tag>
          </template>
          
          <!-- 图片 -->
          <template v-else-if="column.type === 'image'">
            <el-image
              v-if="row[column.prop]"
              :src="row[column.prop]"
              :preview-src-list="[row[column.prop]]"
              fit="cover"
              style="width: 50px; height: 50px"
            />
            <span v-else>-</span>
          </template>
          
          <!-- 默认显示 -->
          <template v-else>
            {{ row[column.prop] !== null && row[column.prop] !== undefined ? row[column.prop] : '-' }}
          </template>
        </template>
      </el-table-column>
      
      <!-- 操作列 -->
      <el-table-column
        v-if="showAction"
        label="操作"
        :width="actionWidth"
        align="center"
        fixed="right"
      >
        <template #default="{ row, $index }">
          <slot name="action" :row="row" :index="$index" />
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="showPagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'

const props = defineProps({
  // 是否显示搜索表单
  searchable: {
    type: Boolean,
    default: true
  },
  // 搜索配置
  searchColumns: {
    type: Array,
    default: () => []
  },
  // 表格列配置
  columns: {
    type: Array,
    required: true
  },
  // 数据加载函数
  request: {
    type: Function,
    required: true
  },
  // 是否显示工具栏
  showToolbar: {
    type: Boolean,
    default: false
  },
  // 是否显示选择列
  selection: {
    type: Boolean,
    default: false
  },
  // 是否显示序号列
  showIndex: {
    type: Boolean,
    default: true
  },
  // 是否显示操作列
  showAction: {
    type: Boolean,
    default: true
  },
  // 操作列宽度
  actionWidth: {
    type: [String, Number],
    default: 200
  },
  // 是否显示分页
  showPagination: {
    type: Boolean,
    default: true
  },
  // 默认分页大小
  defaultPageSize: {
    type: Number,
    default: 20
  },
  // 枚举配置
  enums: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['selection-change', 'search', 'reset'])

// 搜索表单
const searchForm = reactive({})
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(props.defaultPageSize)
const total = ref(0)
const selectedRows = ref([])

// 计算表格列（排除search-only的列）
const tableColumns = computed(() => {
  return props.columns.filter(col => !col.searchOnly)
})

// 获取枚举标签类型
const getEnumTagType = (enumKey, value) => {
  const enumConfig = props.enums[enumKey]
  if (!enumConfig) return ''
  const item = enumConfig.find(item => item.value === value)
  return item?.tagType || ''
}

// 获取枚举标签
const getEnumLabel = (enumKey, value) => {
  const enumConfig = props.enums[enumKey]
  if (!enumConfig) return value
  const item = enumConfig.find(item => item.value === value)
  return item?.label || value
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    // 处理日期范围
    props.searchColumns.forEach(col => {
      if (col.type === 'daterange' && searchForm[col.prop]) {
        const [start, end] = searchForm[col.prop]
        params[`${col.prop}Start`] = start
        params[`${col.prop}End`] = end
        delete params[col.prop]
      }
    })
    
    const res = await props.request(params)
    tableData.value = res.list || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadData()
  emit('search', searchForm)
}

// 重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = undefined
  })
  currentPage.value = 1
  loadData()
  emit('reset')
}

// 分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}

// 页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  loadData()
}

// 选择变化
const handleSelectionChange = (val) => {
  selectedRows.value = val
  emit('selection-change', val)
}

// 刷新数据
const refresh = () => {
  loadData()
}

// 获取选中数据
const getSelection = () => {
  return selectedRows.value
}

// 初始化
onMounted(() => {
  // 初始化搜索表单
  props.searchColumns.forEach(col => {
    if (col.defaultValue !== undefined) {
      searchForm[col.prop] = col.defaultValue
    }
  })
  loadData()
})

// 暴露方法
defineExpose({
  refresh,
  getSelection,
  clearSelection: () => {
    selectedRows.value = []
  }
})
</script>

<style scoped>
.pro-table {
  .search-form {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #f5f7fa;
    border-radius: 4px;
  }
  
  .toolbar {
    margin-bottom: 20px;
  }
  
  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
