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
      <view v-if="!hideUser" class="yd-search-form-item">
        <view class="yd-search-form-label">
          签到用户
        </view>
        <wd-input v-model="formData.nickname" placeholder="请输入签到用户" clearable />
      </view>
      <view class="yd-search-form-item">
        <view class="yd-search-form-label">
          签到天数
        </view>
        <wd-input v-model.number="formData.day" type="number" placeholder="请输入签到天数" clearable />
      </view>
      <yd-search-date-range v-model="formData.createTime" label="签到时间" />
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

defineProps<{
  hideUser?: boolean // 嵌入会员详情时隐藏「签到用户」筛选：用户已固定，再按昵称过滤会很奇怪
}>()

const emit = defineEmits<{
  search: [data: Record<string, any>]
  reset: []
}>()

const visible = ref(false) // 搜索弹窗显示状态
const formData = reactive({
  nickname: undefined as string | undefined,
  day: undefined as number | undefined,
  createTime: [undefined, undefined] as [number | undefined, number | undefined],
}) // 搜索表单数据

/** 搜索条件 placeholder 拼接 */
const placeholder = computed(() => {
  const conditions: string[] = []
  if (formData.nickname) {
    conditions.push(`用户:${formData.nickname}`)
  }
  if (formData.day !== undefined && formData.day !== null) {
    conditions.push(`天数:${formData.day}`)
  }
  if (formData.createTime[0] && formData.createTime[1]) {
    conditions.push(`签到:${formatDate(formData.createTime[0])}~${formatDate(formData.createTime[1])}`)
  }
  return conditions.length > 0 ? conditions.join(' | ') : '搜索签到记录'
})

/** 搜索按钮操作 */
function handleSearch() {
  visible.value = false
  emit('search', {
    ...formData,
    createTime: formatDateRange(formData.createTime),
  })
}

/** 重置按钮操作 */
function handleReset() {
  formData.nickname = undefined
  formData.day = undefined
  formData.createTime = [undefined, undefined]
  visible.value = false
  emit('reset')
}
</script>
