<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="积分商城"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 分页列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无积分商城活动"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white p-24rpx shadow-sm"
          @click="handleDetail(item)"
        >
          <view class="flex items-start gap-20rpx">
            <view v-if="item.picUrl" class="shrink-0">
              <wd-img :src="item.picUrl" width="112rpx" height="112rpx" radius="8rpx" mode="aspectFill" />
            </view>
            <view class="min-w-0 flex-1">
              <view class="mb-12rpx flex items-center justify-between gap-16rpx">
                <text class="min-w-0 flex-1 truncate text-30rpx text-[#333] font-semibold">
                  {{ item.spuName || `商品 #${item.spuId}` }}
                </text>
                <dict-tag v-if="item.status != null" class="shrink-0" :type="DICT_TYPE.COMMON_STATUS" :value="item.status" />
              </view>
              <view class="flex items-center justify-between text-26rpx text-[#666]">
                <text>积分：{{ item.point ?? '-' }}</text>
                <text>库存：{{ item.stock ?? '-' }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['promotion:point-activity:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { PromotionPointActivity } from '@/api/mall/promotion/point'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getPromotionPointActivityPage } from '@/api/mall/promotion/point'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const list = ref<PromotionPointActivity[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询积分商城活动列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getPromotionPointActivityPage({ ...queryParams.value, pageNo, pageSize })
    pagingRef.value?.completeByTotal(data.list, data.total)
  } catch {
    pagingRef.value?.complete(false)
  }
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

/** 新增积分商城活动 */
function handleAdd() {
  uni.navigateTo({ url: '/pages-mall/promotion/point/activity/form/index' })
}

/** 查看详情 */
function handleDetail(item: PromotionPointActivity) {
  uni.navigateTo({ url: `/pages-mall/promotion/point/activity/detail/index?id=${item.id}` })
}

/** 初始化 */
onMounted(() => {
  uni.$on('mall:promotion-point-activity:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('mall:promotion-point-activity:reload', reload)
})
</script>
