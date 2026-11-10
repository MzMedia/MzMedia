<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="商品评论"
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
      empty-view-text="暂无评论数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white p-24rpx shadow-sm"
          @click="handleDetail(item)"
        >
          <view class="mb-12rpx flex items-start justify-between gap-16rpx">
            <view class="min-w-0 flex-1 truncate text-30rpx text-[#333] font-semibold">
              {{ item.userNickname || '匿名用户' }}
            </view>
            <wd-tag :type="item.visible ? 'success' : 'info'" plain>
              {{ item.visible ? '显示' : '隐藏' }}
            </wd-tag>
          </view>
          <view class="mb-12rpx text-28rpx text-[#666] leading-relaxed">
            {{ item.content || '-' }}
          </view>
          <view class="mb-8rpx flex flex-wrap items-center gap-x-24rpx gap-y-4rpx text-26rpx text-[#999]">
            <text>综合：{{ item.scores ?? '-' }}</text>
            <text>商品分：{{ item.descriptionScores ?? '-' }}</text>
            <text>服务分：{{ item.benefitScores ?? '-' }}</text>
            <text>{{ item.replyStatus ? '已回复' : '未回复' }}</text>
          </view>
          <view class="flex items-center text-26rpx text-[#999]">
            <text class="mr-8rpx shrink-0">商品：</text>
            <text class="truncate">{{ item.spuName || '-' }}</text>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增虚拟评论按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['product:comment:update'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { ProductComment } from '@/api/mall/product/comment'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getProductCommentPage } from '@/api/mall/product/comment'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const list = ref<ProductComment[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询评论列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const data = await getProductCommentPage({ ...queryParams.value, pageNo, pageSize })
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

/** 新增虚拟评论 */
function handleAdd() {
  uni.navigateTo({ url: '/pages-mall/product/comment/form/index' })
}

/** 查看详情 */
function handleDetail(item: ProductComment) {
  uni.navigateTo({ url: `/pages-mall/product/comment/detail/index?id=${item.id}` })
}

/** 初始化 */
onMounted(() => {
  uni.$on('mall:product-comment:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('mall:product-comment:reload', reload)
})
</script>
