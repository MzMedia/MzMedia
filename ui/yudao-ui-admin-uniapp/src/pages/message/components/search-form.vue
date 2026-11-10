<template>
  <!-- 搜索框入口 -->
  <view class="flex items-center bg-white pr-30rpx">
    <view class="flex-1" @click="visible = true">
      <wd-search :placeholder="placeholder" hide-cancel disabled />
    </view>
    <view class="text-28rpx text-[#1890ff]" @click="handleReadAll">
      全部已读
    </view>
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
          已读状态
        </view>
        <wd-radio-group v-model="formData.readStatus" type="button">
          <wd-radio :value="-1">
            全部
          </wd-radio>
          <wd-radio :value="true">
            已读
          </wd-radio>
          <wd-radio :value="false">
            未读
          </wd-radio>
        </wd-radio-group>
      </view>
      <yd-search-date-range v-model="formData.createTime" label="发送时间" />
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
  readAll: []
}>()

const visible = ref(false) // 搜索弹窗显示状态
const formData = reactive({
  readStatus: -1 as -1 | boolean, // -1 表示全部, true 已读, false 未读
  createTime: [undefined, undefined] as [number | undefined, number | undefined],
}) // 搜索表单数据

/** 搜索条件 placeholder 拼接 */
const placeholder = computed(() => {
  const conditions: string[] = []
  if (formData.readStatus === true) {
    conditions.push('已读')
  } else if (formData.readStatus === false) {
    conditions.push('未读')
  }
  if (formData.createTime?.[0] && formData.createTime?.[1]) {
    conditions.push(`${formatDate(formData.createTime[0])}~${formatDate(formData.createTime[1])}`)
  }
  return conditions.length > 0 ? conditions.join(' | ') : '搜索消息'
})

/** 全部已读 */
function handleReadAll() {
  emit('readAll')
}

/** 搜索按钮操作 */
function handleSearch() {
  visible.value = false
  emit('search', {
    readStatus: formData.readStatus === -1 ? undefined : formData.readStatus,
    createTime: formatDateRange(formData.createTime),
  })
}

/** 重置按钮操作 */
function handleReset() {
  formData.readStatus = -1
  formData.createTime = [undefined, undefined]
  visible.value = false
  emit('reset')
}
</script>
