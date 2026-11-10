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
          用户标识
        </view>
        <wd-input v-model="formData.openid" placeholder="请输入用户标识" clearable />
      </view>
      <view class="yd-search-form-item">
        <view class="yd-search-form-label">
          昵称
        </view>
        <wd-input v-model="formData.nickname" placeholder="请输入昵称" clearable />
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
  openid: undefined as string | undefined,
  nickname: undefined as string | undefined,
}) // 搜索表单数据

const placeholder = computed(() => {
  const conditions: string[] = []
  if (formData.openid) {
    conditions.push(`标识:${formData.openid}`)
  }
  if (formData.nickname) {
    conditions.push(`昵称:${formData.nickname}`)
  }
  return conditions.length > 0 ? conditions.join(' | ') : '搜索粉丝'
})

/** 搜索按钮操作 */
function handleSearch() {
  visible.value = false
  emit('search', {
    openid: formData.openid || undefined,
    nickname: formData.nickname || undefined,
  })
}

/** 重置按钮操作 */
function handleReset() {
  formData.openid = undefined
  formData.nickname = undefined
  visible.value = false
  emit('reset')
}
</script>
