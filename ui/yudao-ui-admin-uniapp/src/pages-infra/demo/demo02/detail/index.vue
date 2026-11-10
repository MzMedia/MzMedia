<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="示例分类详情"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 详情内容 -->
    <view>
      <wd-cell-group border>
        <wd-cell title="名字" :value="formData?.name ?? '-'" />
        <wd-cell title="父级" :value="parentName" />
        <wd-cell title="创建时间" :value="formatDateTime(formData?.createTime)" />
      </wd-cell-group>
    </view>

    <!-- 底部操作按钮 -->
    <view class="yd-detail-footer">
      <view class="yd-detail-footer-actions">
        <wd-button
          v-if="hasAccessByCodes(['infra:demo02-category:update'])"
          class="flex-1" type="warning" @click="handleEdit"
        >
          编辑
        </wd-button>
        <wd-button
          v-if="hasAccessByCodes(['infra:demo02-category:delete'])"
          class="flex-1" type="danger" :loading="deleting" @click="handleDelete"
        >
          删除
        </wd-button>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { Demo02Category } from '@/api/infra/demo/demo02'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onShow } from '@dcloudio/uni-app'
import { computed, ref } from 'vue'
import { deleteDemo02Category, getDemo02Category, getDemo02CategoryList } from '@/api/infra/demo/demo02'
import { useAccess } from '@/hooks/useAccess'
import { delay, navigateBackPlus } from '@/utils'
import { formatDateTime } from '@/utils/date'

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
const formData = ref<Demo02Category>() // 详情数据
const categoryList = ref<Demo02Category[]>([]) // 全部分类（解析父级名）
const deleting = ref(false) // 删除状态

/** 父级名称 */
const parentName = computed(() => {
  if (!formData.value?.parentId) {
    return '顶级分类'
  }
  return categoryList.value.find(item => item.id === formData.value?.parentId)?.name ?? '-'
})

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-infra/demo/demo02/index')
}

/** 加载详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  const [detail, list] = await Promise.all([
    getDemo02Category(props.id),
    getDemo02CategoryList(),
  ])
  formData.value = detail
  categoryList.value = list
}

/** 编辑 */
function handleEdit() {
  uni.navigateTo({ url: `/pages-infra/demo/demo02/form/index?id=${props.id}` })
}

/** 删除 */
async function handleDelete() {
  try {
    await dialog.confirm({ title: '提示', msg: '确定要删除该示例分类吗？' })
  } catch {
    return
  }
  deleting.value = true
  try {
    await deleteDemo02Category(props.id)
    toast.success('删除成功')
    uni.$emit('infra:demo02-category:reload')
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
