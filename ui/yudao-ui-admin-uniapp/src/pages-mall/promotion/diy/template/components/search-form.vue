<template>
  <!-- 搜索框入口 -->
  <view @click="visible = true">
    <wd-search :placeholder="placeholder" hide-cancel disabled />
  </view>

  <!-- 搜索弹窗 -->
  <wd-popup
    v-model="visible"
    position="top"
    :custom-style="getTopPopupStyle()"
    :modal-style="getTopPopupModalStyle()"
    @close="visible = false"
  >
    <view class="yd-search-form-container">
      <view class="yd-search-form-item">
        <view class="yd-search-form-label">
          模板名称
        </view>
        <wd-input v-model="formData.name" placeholder="请输入模板名称" clearable />
      </view>
      <view class="yd-search-form-item">
        <view class="yd-search-form-label">
          是否使用
        </view>
        <wd-radio-group v-model="formData.used" type="button">
          <wd-radio :value="-1">
            全部
          </wd-radio>
          <wd-radio :value="1">
            已使用
          </wd-radio>
          <wd-radio :value="0">
            未使用
          </wd-radio>
        </wd-radio-group>
      </view>
      <yd-search-date-range v-model="formData.usedTime" label="使用时间" />
      <yd-search-date-range v-model="formData.createTime" label="创建时间" />
      <view class="yd-search-form-actions">
        <wd-button class="flex-1" variant="plain" @click="handleReset">
          重置
        </wd-button>
        <wd-button class="flex-1" type="primary" @click="handleSearch">
          搜索
        </wd-button>
      </view>
    </view>
  </wd-popup>
</template>

<script lang="ts" setup>
import { computed, reactive, ref } from 'vue'
import { getTopPopupModalStyle, getTopPopupStyle } from '@/utils'
import { formatDate, formatDateRange } from '@/utils/date'

const emit = defineEmits<{
  search: [data: Record<string, any>]
  reset: []
}>()

const visible = ref(false) // 搜索弹窗显示状态
const formData = reactive({
  name: undefined as string | undefined,
  used: -1, // 是否使用，-1=全部 1=已使用 0=未使用
  usedTime: [undefined, undefined] as [number | undefined, number | undefined], // 使用时间范围
  createTime: [undefined, undefined] as [number | undefined, number | undefined], // 创建时间范围
}) // 搜索表单数据

/** 搜索条件 placeholder 拼接 */
const placeholder = computed(() => {
  const conditions: string[] = []
  if (formData.name) {
    conditions.push(`名称:${formData.name}`)
  }
  if (formData.used !== -1) {
    conditions.push(`使用:${formData.used === 1 ? '已使用' : '未使用'}`)
  }
  if (formData.createTime?.[0] && formData.createTime?.[1]) {
    conditions.push(`时间:${formatDate(formData.createTime[0])}~${formatDate(formData.createTime[1])}`)
  }
  return conditions.length > 0 ? conditions.join(' | ') : '搜索装修模板'
})

/** 搜索按钮操作 */
function handleSearch() {
  visible.value = false
  emit('search', {
    name: formData.name || undefined,
    used: formData.used === -1 ? undefined : formData.used === 1,
    usedTime: formatDateRange(formData.usedTime),
    createTime: formatDateRange(formData.createTime),
  })
}

/** 重置按钮操作 */
function handleReset() {
  formData.name = undefined
  formData.used = -1
  formData.usedTime = [undefined, undefined]
  formData.createTime = [undefined, undefined]
  visible.value = false
  emit('reset')
}
</script>
