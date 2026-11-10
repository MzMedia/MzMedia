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
          用户昵称
        </view>
        <wd-input v-model="formData.userNickname" placeholder="请输入用户昵称" clearable />
      </view>
      <view class="yd-search-form-item">
        <view class="yd-search-form-label">
          商品名称
        </view>
        <wd-input v-model="formData.spuName" placeholder="请输入商品名称" clearable />
      </view>
      <view class="yd-search-form-item">
        <view class="yd-search-form-label">
          订单编号
        </view>
        <wd-input v-model="formData.orderId" type="number" placeholder="请输入订单编号" clearable />
      </view>
      <view class="yd-search-form-item">
        <view class="yd-search-form-label">
          回复状态
        </view>
        <wd-radio-group v-model="formData.replyStatus" type="button">
          <wd-radio :value="-1">
            全部
          </wd-radio>
          <wd-radio :value="1">
            已回复
          </wd-radio>
          <wd-radio :value="0">
            未回复
          </wd-radio>
        </wd-radio-group>
      </view>
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
  userNickname: undefined as string | undefined,
  spuName: undefined as string | undefined,
  orderId: undefined as string | undefined,
  replyStatus: -1, // -1=全部 1=已回复 0=未回复
  createTime: [undefined, undefined] as [number | undefined, number | undefined],
}) // 搜索表单数据

/** 搜索条件 placeholder 拼接 */
const placeholder = computed(() => {
  const conditions: string[] = []
  if (formData.userNickname) {
    conditions.push(`昵称:${formData.userNickname}`)
  }
  if (formData.spuName) {
    conditions.push(`商品:${formData.spuName}`)
  }
  if (formData.orderId) {
    conditions.push(`订单:${formData.orderId}`)
  }
  if (formData.replyStatus !== -1) {
    conditions.push(`回复:${formData.replyStatus === 1 ? '已回复' : '未回复'}`)
  }
  if (formData.createTime?.[0] && formData.createTime?.[1]) {
    conditions.push(`时间:${formatDate(formData.createTime[0])}~${formatDate(formData.createTime[1])}`)
  }
  return conditions.length > 0 ? conditions.join(' | ') : '搜索评论'
})

/** 搜索按钮操作 */
function handleSearch() {
  visible.value = false
  emit('search', {
    userNickname: formData.userNickname || undefined,
    spuName: formData.spuName || undefined,
    orderId: formData.orderId || undefined,
    replyStatus: formData.replyStatus === -1 ? undefined : formData.replyStatus === 1,
    createTime: formatDateRange(formData.createTime),
  })
}

/** 重置按钮操作 */
function handleReset() {
  formData.userNickname = undefined
  formData.spuName = undefined
  formData.orderId = undefined
  formData.replyStatus = -1
  formData.createTime = [undefined, undefined]
  visible.value = false
  emit('reset')
}
</script>
