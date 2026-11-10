<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="示例联系人详情"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 详情内容 -->
    <view>
      <wd-cell-group border>
        <wd-cell title="名字" :value="formData?.name ?? '-'" />
        <wd-cell title="性别">
          <dict-tag :type="DICT_TYPE.SYSTEM_USER_SEX" :value="formData?.sex" />
        </wd-cell>
        <wd-cell title="出生年" :value="formData?.birthday ? formatDate(formData.birthday) : '-'" />
        <wd-cell title="简介" :value="formData?.description ?? '-'" />
        <wd-cell title="头像">
          <wd-img v-if="formData?.avatar" :src="formData.avatar" width="120rpx" height="120rpx" round />
          <text v-else class="text-28rpx text-[#999]">-</text>
        </wd-cell>
        <wd-cell title="创建时间" :value="formatDateTime(formData?.createTime)" />
      </wd-cell-group>
    </view>

    <!-- 底部操作按钮 -->
    <view class="yd-detail-footer">
      <view class="yd-detail-footer-actions">
        <wd-button
          v-if="hasAccessByCodes(['infra:demo01-contact:update'])"
          class="flex-1" type="warning" @click="handleEdit"
        >
          编辑
        </wd-button>
        <wd-button
          v-if="hasAccessByCodes(['infra:demo01-contact:delete'])"
          class="flex-1" type="danger" :loading="deleting" @click="handleDelete"
        >
          删除
        </wd-button>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { Demo01Contact } from '@/api/infra/demo/demo01'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { deleteDemo01Contact, getDemo01Contact } from '@/api/infra/demo/demo01'
import { useAccess } from '@/hooks/useAccess'
import { delay, navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDate, formatDateTime } from '@/utils/date'

const props = defineProps<{
  id?: number | any
}>()

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const toast = useToast()
const dialog = useDialog()
const formData = ref<Demo01Contact>() // 详情数据
const deleting = ref(false) // 删除状态

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-infra/demo/demo01/index')
}

/** 加载详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  formData.value = await getDemo01Contact(props.id)
}

/** 编辑 */
function handleEdit() {
  uni.navigateTo({ url: `/pages-infra/demo/demo01/form/index?id=${props.id}` })
}

/** 删除 */
async function handleDelete() {
  try {
    await dialog.confirm({ title: '提示', msg: '确定要删除该示例联系人吗？' })
  } catch {
    return
  }
  deleting.value = true
  try {
    await deleteDemo01Contact(props.id)
    toast.success('删除成功')
    uni.$emit('infra:demo01-contact:reload')
    delay(handleBack)
  } finally {
    deleting.value = false
  }
}

/** 进入页面加载（含编辑返回后刷新） */
onShow(() => {
  getDetail()
})
</script>
