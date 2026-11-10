<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="菜单详情"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 详情内容 -->
    <view>
      <wd-cell-group border>
        <wd-cell title="菜单名称" :value="formData?.name || '-'" />
        <wd-cell title="菜单类型">
          <dict-tag :type="DICT_TYPE.SYSTEM_MENU_TYPE" :value="formData?.type" />
        </wd-cell>
        <wd-cell title="上级菜单" :value="parentMenuName" />
        <wd-cell title="显示排序" :value="formData?.sort" />
        <wd-cell title="路由地址" :value="formData?.path || '-'" />
        <wd-cell v-if="formData?.type === SystemMenuTypeEnum.MENU" title="组件路径" :value="formData?.component || '-'" />
        <wd-cell v-if="formData?.type === SystemMenuTypeEnum.MENU" title="组件名称" :value="formData?.componentName || '-'" />
        <wd-cell v-if="formData?.type !== SystemMenuTypeEnum.DIR" title="权限标识" :value="formData?.permission || '-'" />
        <wd-cell title="菜单状态">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="formData?.status" />
        </wd-cell>
        <wd-cell v-if="formData?.type !== SystemMenuTypeEnum.BUTTON" title="显示状态">
          <wd-tag v-if="formData?.visible" type="success" variant="plain">
            显示
          </wd-tag>
          <wd-tag v-else type="warning" variant="plain">
            隐藏
          </wd-tag>
        </wd-cell>
        <wd-cell v-if="formData?.type === SystemMenuTypeEnum.MENU" title="缓存状态">
          <wd-tag v-if="formData?.keepAlive" type="success" variant="plain">
            缓存
          </wd-tag>
          <wd-tag v-else type="default" variant="plain">
            不缓存
          </wd-tag>
        </wd-cell>
        <wd-cell title="创建时间" :value="formatDateTime(formData?.createTime)" />
      </wd-cell-group>
    </view>

    <!-- 底部操作按钮 -->
    <view class="yd-detail-footer">
      <view class="yd-detail-footer-actions">
        <wd-button
          v-if="hasAccessByCodes(['system:menu:update'])"
          class="flex-1" type="warning" @click="handleEdit"
        >
          编辑
        </wd-button>
        <wd-button
          v-if="hasAccessByCodes(['system:menu:delete'])"
          class="flex-1" type="danger" :loading="deleting" @click="handleDelete"
        >
          删除
        </wd-button>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { Menu } from '@/api/system/menu'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { deleteMenu, getMenu, getSimpleMenuList } from '@/api/system/menu'
import { useAccess } from '@/hooks/useAccess'
import { delay, navigateBackPlus } from '@/utils'
import { DICT_TYPE, SystemMenuTypeEnum } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'

const props = defineProps<{
  id: number | any
}>()

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const toast = useToast()
const dialog = useDialog()
const { hasAccessByCodes } = useAccess()
const formData = ref<Menu>() // 详情数据
const deleting = ref(false) // 删除状态
const parentMenuName = ref('-') // 上级菜单名称链

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-system/menu/index')
}

/** 加载菜单详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  toast.loading('加载中...')
  try {
    formData.value = await getMenu(props.id)
    const parentId = formData.value?.parentId
    if (parentId === 0) {
      parentMenuName.value = '主类目'
    } else if (parentId) {
      const menuList = await getSimpleMenuList()
      parentMenuName.value = getParentMenuName(menuList, parentId)
    } else {
      parentMenuName.value = '-'
    }
  } finally {
    toast.close()
  }
}

/** 获取父菜单名称链 */
function getParentMenuName(menuList: Menu[], parentId: number) {
  const menuMap = new Map<number, Menu>()
  menuList.forEach((item) => {
    if (item.id !== undefined) {
      menuMap.set(item.id, item)
    }
  })

  // 通过 parentId 逐级查找父菜单，构建名称链
  const names: string[] = []
  const visitedIds = new Set<number>()
  let currentParentId = parentId
  while (currentParentId && !visitedIds.has(currentParentId)) {
    visitedIds.add(currentParentId)
    const parent = menuMap.get(currentParentId)
    if (!parent) {
      break
    }
    names.unshift(parent.name)
    currentParentId = parent.parentId
  }
  return names.length > 0 ? names.join(' / ') : '-'
}

/** 编辑菜单 */
function handleEdit() {
  uni.navigateTo({
    url: `/pages-system/menu/form/index?id=${props.id}`,
  })
}

/** 删除菜单 */
async function handleDelete() {
  if (!props.id) {
    return
  }
  try {
    await dialog.confirm({
      title: '提示',
      msg: '确定要删除该菜单吗？',
    })
  } catch {
    return
  }
  // 执行删除
  deleting.value = true
  try {
    await deleteMenu(props.id)
    toast.success('删除成功')
    uni.$emit('system:menu:reload')
    delay(handleBack)
  } finally {
    deleting.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getDetail()
  uni.$on('system:menu:reload', getDetail)
})

/** 卸载 */
onUnload(() => {
  uni.$off('system:menu:reload', getDetail)
})
</script>
