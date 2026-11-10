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
          名称
        </view>
        <wd-input v-model="formData.name" placeholder="请输入名称" clearable />
      </view>
      <view class="yd-search-form-item">
        <view class="yd-search-form-label">
          微信号
        </view>
        <wd-input v-model="formData.account" placeholder="请输入微信号" clearable />
      </view>
      <view class="yd-search-form-item">
        <view class="yd-search-form-label">
          AppID
        </view>
        <wd-input v-model="formData.appId" placeholder="请输入 AppID" clearable />
      </view>
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

const emit = defineEmits<{
  search: [data: Record<string, any>]
  reset: []
}>()

const visible = ref(false) // 搜索弹窗显示状态
const formData = reactive({
  name: undefined as string | undefined,
  account: undefined as string | undefined,
  appId: undefined as string | undefined,
}) // 搜索表单数据

const placeholder = computed(() => {
  const conditions: string[] = []
  if (formData.name) {
    conditions.push(`名称:${formData.name}`)
  }
  if (formData.account) {
    conditions.push(`微信号:${formData.account}`)
  }
  if (formData.appId) {
    conditions.push(`AppID:${formData.appId}`)
  }
  return conditions.length > 0 ? conditions.join(' | ') : '搜索公众号账号'
})

/** 搜索按钮操作 */
function handleSearch() {
  visible.value = false
  emit('search', {
    name: formData.name || undefined,
    account: formData.account || undefined,
    appId: formData.appId || undefined,
  })
}

/** 重置按钮操作 */
function handleReset() {
  formData.name = undefined
  formData.account = undefined
  formData.appId = undefined
  visible.value = false
  emit('reset')
}
</script>
