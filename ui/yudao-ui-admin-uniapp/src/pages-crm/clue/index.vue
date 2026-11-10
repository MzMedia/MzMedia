<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="线索管理"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 归属场景 -->
    <view class="bg-white">
      <wd-tabs v-model="sceneTabIndex" shrink @change="handleSceneChange">
        <wd-tab v-for="tab in sceneTabs" :key="tab.value" :title="tab.label" />
      </wd-tabs>
    </view>

    <!-- 线索列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无线索数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view v-for="item in list" :key="item.id" class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm" @click="handleDetail(item)">
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.name }}
            </view>
            <view class="flex flex-wrap justify-end gap-8rpx">
              <dict-tag v-if="item.source !== undefined && item.source !== null" :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="item.source" />
              <dict-tag v-if="item.level !== undefined && item.level !== null" :type="DICT_TYPE.CRM_CUSTOMER_LEVEL" :value="item.level" />
            </view>
          </view>
          <view v-if="item.mobile" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">手机：</text>{{ item.mobile }}
          </view>
          <view v-if="item.ownerUserName" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">负责人：</text>{{ item.ownerUserName }}
          </view>
          <view v-if="item.contactNextTime" class="text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">下次联系：</text>{{ formatDateTime(item.contactNextTime) }}
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab v-if="hasAccessByCodes(['crm:clue:create'])" position="right-bottom" type="primary" :expandable="false" @click="handleAdd" />
  </view>
</template>

<script lang="ts" setup>
import type { Clue } from '@/api/crm/clue'
import { onUnload } from '@dcloudio/uni-app'
import { computed, onMounted, ref } from 'vue'
import { getCluePage } from '@/api/crm/clue'
import { CRM_SCENE_TYPES } from '@/api/crm/permission'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const sceneTabs = CRM_SCENE_TYPES

const { hasAccessByCodes } = useAccess()
const list = ref<Clue[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({ transformStatus: false }) // 查询参数
const sceneTabIndex = ref(0) // 当前归属场景下标
const sceneType = computed(() => sceneTabs[sceneTabIndex.value].value)

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询线索列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getCluePage({ ...queryParams.value, sceneType: sceneType.value, pageNo, pageSize })
    pagingRef.value?.completeByTotal(data.list, data.total)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 归属场景切换 */
function handleSceneChange({ index }: { index: number }) {
  sceneTabIndex.value = index
  reload()
}

/** 搜索按钮操作 */
function handleQuery(data: Record<string, any> = { transformStatus: false }) {
  queryParams.value = { ...data }
  reload()
}

/** 重置按钮操作 */
function handleReset() {
  handleQuery()
}

/** 重新加载 */
function reload() {
  pagingRef.value?.reload()
}

/** 新增线索 */
function handleAdd() {
  uni.navigateTo({ url: '/pages-crm/clue/form/index' })
}

/** 查看详情 */
function handleDetail(item: Clue) {
  uni.navigateTo({ url: `/pages-crm/clue/detail/index?id=${item.id}` })
}

/** 初始化 */
onMounted(() => {
  uni.$on('crm:clue:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('crm:clue:reload', reload)
})
</script>
