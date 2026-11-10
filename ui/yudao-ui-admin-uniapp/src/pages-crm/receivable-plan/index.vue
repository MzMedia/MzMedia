<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="回款计划"
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

    <!-- 回款计划列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无回款计划数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view v-for="item in list" :key="item.id" class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm" @click="handleDetail(item)">
          <view class="mb-16rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1 truncate text-32rpx text-[#333] font-semibold">
              {{ item.customerName || '-' }}
            </view>
            <view class="flex flex-wrap justify-end gap-8rpx">
              <dict-tag v-if="item.returnType !== undefined && item.returnType !== null" :type="DICT_TYPE.CRM_RECEIVABLE_RETURN_TYPE" :value="item.returnType" />
            </view>
          </view>
          <view v-if="item.contractNo" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">合同编号：</text>{{ item.contractNo }}
          </view>
          <view v-if="item.period !== undefined && item.period !== null" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">期数：</text>{{ item.period }}
          </view>
          <view v-if="item.price !== undefined && item.price !== null" class="mb-12rpx text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">金额：</text>{{ formatMoney(item.price) }}
          </view>
          <view v-if="item.returnTime" class="text-28rpx text-[#666]">
            <text class="mr-8rpx text-[#999]">日期：</text>{{ formatDate(item.returnTime) }}
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab v-if="hasAccessByCodes(['crm:receivable-plan:create'])" position="right-bottom" type="primary" :expandable="false" @click="handleAdd" />
  </view>
</template>

<script lang="ts" setup>
import type { ReceivablePlan } from '@/api/crm/receivable/plan'
import { onUnload } from '@dcloudio/uni-app'
import { computed, onMounted, ref } from 'vue'
import { getReceivablePlanPage } from '@/api/crm/receivable/plan'
import { CRM_SCENE_TYPES, CrmSceneTypeEnum } from '@/api/crm/permission'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDate } from '@/utils/date'
import { formatMoney } from '@/utils/format'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const sceneTabs = CRM_SCENE_TYPES.filter(item => item.value !== CrmSceneTypeEnum.INVOLVED)

const { hasAccessByCodes } = useAccess()
const list = ref<ReceivablePlan[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数
const sceneTabIndex = ref(0) // 当前归属场景下标
const sceneType = computed(() => sceneTabs[sceneTabIndex.value].value)

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询回款计划列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getReceivablePlanPage({ ...queryParams.value, sceneType: sceneType.value, pageNo, pageSize })
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
function handleQuery(data?: Record<string, any>) {
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

/** 新增回款计划 */
function handleAdd() {
  uni.navigateTo({ url: '/pages-crm/receivable-plan/form/index' })
}

/** 查看详情 */
function handleDetail(item: ReceivablePlan) {
  uni.navigateTo({ url: `/pages-crm/receivable-plan/detail/index?id=${item.id}` })
}

/** 初始化 */
onMounted(() => {
  uni.$on('crm:receivablePlan:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('crm:receivablePlan:reload', reload)
})
</script>
