<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="发起申请"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索框 -->
    <wd-search
      v-model="searchName"
      placeholder="请输入流程名称"
      placeholder-left
      hide-cancel
      @search="handleSearch"
      @clear="handleSearch"
    />

    <!-- 分类标签 -->
    <wd-tabs
      v-model="activeCategory"
      slidable="always"
      sticky
      @click="handleTabClick"
    >
      <wd-tab v-for="item in availableCategories" :key="item.code" :title="item.name" :name="item.code" />
    </wd-tabs>

    <!-- 流程定义列表 -->
    <scroll-view
      scroll-y
      class="h-[calc(100vh-320rpx)]"
      :scroll-into-view="scrollIntoView"
      scroll-with-animation
      @scroll="handleScroll"
    >
      <view
        v-for="item in availableCategories"
        :id="`category-${item.code}`"
        :key="item.code"
        class="category-section mx-24rpx mt-24rpx"
        :data-category="item.code"
      >
        <!-- 分类标题 -->
        <view class="mb-16rpx flex items-center">
          <text class="text-28rpx text-[#333] font-bold">{{ item.name }}</text>
        </view>
        <!-- 流程列表 -->
        <view class="overflow-hidden rounded-16rpx bg-white">
          <view
            v-for="definition in groupedDefinitions[item.code]"
            :key="definition.id"
            class="flex items-center border-b border-[#f5f5f5] p-24rpx last:border-b-0"
            @click="handleSelect(definition)"
          >
            <wd-img
              v-if="definition.icon"
              :src="definition.icon"
              width="64rpx"
              height="64rpx"
              mode="aspectFit"
              radius="24rpx"
              class="mr-16rpx"
            />
            <view
              v-else
              class="mr-16rpx h-64rpx w-64rpx flex items-center justify-center rounded-12rpx"
              :style="{ backgroundColor: getIconColor(definition.name) }"
            >
              <text class="text-24rpx text-white font-bold">{{ getIconText(definition.name) }}</text>
            </view>
            <text class="text-28rpx text-[#333]">{{ definition.name }}</text>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="availableCategories.length === 0" class="py-100rpx">
        <wd-empty icon="content" tip="暂无可发起的流程" />
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import type { Category } from '@/api/bpm/category'
import type { ProcessDefinition } from '@/api/bpm/definition'
import { onLoad } from '@dcloudio/uni-app'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, nextTick, ref } from 'vue'
import { getCategorySimpleList } from '@/api/bpm/category'
import { getProcessDefinitionList } from '@/api/bpm/definition'
import { getProcessInstance } from '@/api/bpm/processInstance'
import { getMobileFormCustomPath } from '@/pages-bpm/utils'
import { navigateBackPlus } from '@/utils'
import { BpmModelFormType } from '@/utils/constants'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const toast = useToast()

const searchName = ref('')
const activeCategory = ref('')
const categoryList = ref<Category[]>([])
const categoryPositions = ref<{ code: string, top: number }[]>([]) // 分类区域位置信息（用于滚动时自动切换 tab）
const scrollIntoView = ref('')
const isTabClicking = ref(false) // 是否正在通过点击 tab 触发滚动（避免滚动事件反向更新 tab）

const definitionList = ref<ProcessDefinition[]>([])

/** 根据流程名称获取图标背景色 */
function getIconColor(name: string): string {
  const iconColors = ['#D98469', '#7BC67C', '#4A7FEB', '#9B7FEB', '#4A9DEB']
  // 根据名称 hashcode 取模选择颜色
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = (hash * 31 + name.charCodeAt(i)) | 0
  }
  return iconColors[Math.abs(hash) % iconColors.length]
}

/** 获取流程名称的前两个字符作为图标文字 */
function getIconText(name: string): string {
  return name?.slice(0, 2) || ''
}

/** 过滤后的流程定义 */
const filteredDefinitions = computed(() => {
  if (!searchName.value.trim()) {
    return definitionList.value
  }
  return definitionList.value.filter(item =>
    item.name.toLowerCase().includes(searchName.value.toLowerCase()),
  )
})

/** 按分类分组的流程定义 */
const groupedDefinitions = computed<Record<string, ProcessDefinition[]>>(() => {
  const grouped: Record<string, ProcessDefinition[]> = {}
  filteredDefinitions.value.forEach((item) => {
    if (!item.category)
      return
    if (!grouped[item.category])
      grouped[item.category] = []
    grouped[item.category].push(item)
  })
  return grouped
})

/** 仅保留含流程定义的分类（空分类不展示 tab 与卡片） */
const availableCategories = computed<Category[]>(() =>
  categoryList.value.filter(item => groupedDefinitions.value[item.code]?.length),
)

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages/bpm/index')
}

/** 更新流程定义搜索结果位置 */
async function handleSearch() {
  // 搜索后重新计算分类位置
  await nextTick()
  updateCategoryPositions()
}

/** Tab 点击 */
function handleTabClick({ name }: { index: number, name: string }) {
  isTabClicking.value = true
  // 滚动到对应分类
  scrollIntoView.value = ''
  nextTick(() => {
    scrollIntoView.value = `category-${name}`
    // 300ms 后恢复滚动监听
    setTimeout(() => {
      isTabClicking.value = false
    }, 300)
  })
}

/** 滚动事件 - 自动切换 tab */
function handleScroll(e: { detail: { scrollTop: number } }) {
  if (isTabClicking.value || categoryPositions.value.length === 0) {
    return
  }
  // 找到当前滚动位置对应的分类
  const scrollTop = e.detail.scrollTop
  for (let i = categoryPositions.value.length - 1; i >= 0; i--) {
    if (scrollTop >= categoryPositions.value[i].top - 20) {
      if (activeCategory.value !== categoryPositions.value[i].code) {
        activeCategory.value = categoryPositions.value[i].code
      }
      break
    }
  }
}

/** 更新分类区域位置信息 */
function updateCategoryPositions() {
  const query = uni.createSelectorQuery()
  query.selectAll('.category-section').boundingClientRect()
  query.exec((res) => {
    if (res && res[0]) {
      const positions: { code: string, top: number }[] = []
      const firstTop = res[0][0]?.top || 0
      res[0].forEach((item: { top: number, dataset?: { category?: string } }, index: number) => {
        const category = availableCategories.value[index]
        if (!category) {
          return
        }
        positions.push({
          code: category.code,
          top: item.top - firstTop,
        })
      })
      categoryPositions.value = positions
    }
  })
}

/** 选择流程定义 */
function handleSelect(item: ProcessDefinition, processInstanceId?: string) {
  // 情况一：流程表单，跳转到移动端流程表单填写页
  if (item.formType === BpmModelFormType.NORMAL) {
    const restartQuery = processInstanceId ? `&processInstanceId=${processInstanceId}` : ''
    uni.navigateTo({ url: `/pages-bpm/processInstance/create/form?processDefinitionId=${item.id}${restartQuery}` })
    return
  }

  // 情况二：业务表单，跳转到对应的移动端页面
  if (item.formType === BpmModelFormType.CUSTOM) {
    const mobilePath = getMobileFormCustomPath(item.formCustomCreatePath)
    if (mobilePath) {
      uni.navigateTo({ url: mobilePath })
    } else {
      toast.show('该业务表单暂不支持移动端发起')
    }
  }
}

/** 加载分类列表 */
async function loadCategoryList() {
  categoryList.value = await getCategorySimpleList()
}

/** 加载流程定义列表 */
async function loadDefinitionList() {
  definitionList.value = await getProcessDefinitionList({ suspensionState: 1 })
}

/** 初始化 */
onLoad(async (options) => {
  // 分类和流程定义互不依赖，并行加载减少首屏等待时间
  await Promise.all([loadCategoryList(), loadDefinitionList()])

  // 从详情页重新发起时，直接定位原流程定义并进入表单页
  if (options?.processInstanceId) {
    await restartProcessInstance(options.processInstanceId)
    return
  }
  // 默认选中第一个有流程定义的分类
  if (availableCategories.value.length > 0) {
    activeCategory.value = availableCategories.value[0].code
  }
  // 等待 DOM 渲染后计算分类位置
  await nextTick()
  setTimeout(() => {
    updateCategoryPositions()
  }, 100)
})

/** 重新发起流程 */
async function restartProcessInstance(processInstanceId: string) {
  const processInstance = await getProcessInstance(processInstanceId)
  if (!processInstance) {
    toast.show('重新发起流程失败，原因：流程实例不存在')
    return
  }
  const processDefinition = definitionList.value.find(item =>
    item.id === processInstance.processDefinition?.id
    || item.key === processInstance.processDefinition?.key,
  )
  if (!processDefinition) {
    toast.show('重新发起流程失败，原因：流程定义不存在')
    return
  }
  // 复用普通发起入口，带上流程实例编号让表单页回填历史变量
  handleSelect(processDefinition, processInstanceId)
}
</script>
