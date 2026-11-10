<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="导入表"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 数据源 + 表名搜索 -->
    <view class="bg-white">
      <DataSourcePicker v-model="dataSourceConfigId" @change="getList" />
    </view>
    <view class="flex items-center gap-16rpx bg-white px-24rpx pb-20rpx">
      <wd-input v-model="searchName" class="flex-1" placeholder="按表名称搜索" clearable />
      <wd-button size="small" type="primary" @click="getList">
        搜索
      </wd-button>
    </view>

    <!-- 可导入的表（多选；已导入的会被后端过滤） -->
    <scroll-view class="min-h-0 flex-1" scroll-y>
      <view v-if="loading" class="py-80rpx text-center text-26rpx text-[#999]">
        加载中...
      </view>
      <view v-else-if="!tables.length" class="py-80rpx text-center text-26rpx text-[#999]">
        暂无可导入的表
      </view>
      <view v-else class="p-24rpx">
        <view
          v-for="t in tables"
          :key="t.name"
          class="mb-16rpx flex items-center rounded-12rpx bg-white p-24rpx shadow-sm"
          :class="selected.includes(t.name) ? 'bg-[#eef4ff]!' : ''"
          @click="toggle(t.name)"
        >
          <view class="min-w-0 flex-1">
            <view class="truncate text-30rpx" :class="selected.includes(t.name) ? 'text-[#1677ff] font-medium' : 'text-[#333]'">
              {{ t.name }}
            </view>
            <view class="mt-4rpx truncate text-24rpx text-[#999]">
              {{ t.comment || '-' }}
            </view>
          </view>
          <wd-icon v-if="selected.includes(t.name)" name="check" size="36rpx" color="#1677ff" />
        </view>
      </view>
    </scroll-view>

    <!-- 底部导入 -->
    <view class="yd-detail-footer">
      <view class="yd-detail-footer-actions">
        <wd-button
          class="flex-1" type="primary"
          :disabled="!selected.length" :loading="importing"
          @click="handleImport"
        >
          导入{{ selected.length ? ` (${selected.length})` : '' }}
        </wd-button>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { CodegenDbTable } from '@/api/infra/codegen'
import { ref } from 'vue'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { createCodegenList, getCodegenDbTableList } from '@/api/infra/codegen'
import { delay, navigateBackPlus } from '@/utils'
import DataSourcePicker from '@/pages-infra/data-source-config/components/data-source-picker.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const toast = useToast()
const dataSourceConfigId = ref<number>() // 选中数据源
const searchName = ref('') // 表名搜索
const tables = ref<CodegenDbTable[]>([]) // 可导入的表
const selected = ref<string[]>([]) // 已选表名
const loading = ref(false) // 列表加载状态
const importing = ref(false) // 导入中状态

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-infra/codegen/index')
}

/** 切换选中 */
function toggle(name: string) {
  const index = selected.value.indexOf(name)
  if (index >= 0) {
    selected.value.splice(index, 1)
  } else {
    selected.value.push(name)
  }
}

/** 查询可导入的表 */
async function getList() {
  if (dataSourceConfigId.value == null) {
    return
  }
  loading.value = true
  selected.value = []
  try {
    tables.value = await getCodegenDbTableList({
      dataSourceConfigId: dataSourceConfigId.value,
      name: searchName.value || undefined,
    })
  } finally {
    loading.value = false
  }
}

/** 导入选中的表 */
async function handleImport() {
  if (!selected.value.length || dataSourceConfigId.value == null) {
    return
  }
  importing.value = true
  try {
    await createCodegenList({
      dataSourceConfigId: dataSourceConfigId.value,
      tableNames: [...selected.value],
    })
    toast.success('导入成功')
    uni.$emit('infra:codegen:reload')
    delay(handleBack)
  } finally {
    importing.value = false
  }
}
</script>
