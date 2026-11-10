<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="会员等级"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 等级列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="20"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无会员等级数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white shadow-sm"
          @click="handleDetail(item)"
        >
          <view class="p-24rpx">
            <view class="mb-16rpx flex items-center justify-between gap-16rpx">
              <view class="min-w-0 flex items-center gap-16rpx">
                <wd-img
                  v-if="item.icon"
                  :src="item.icon"
                  :width="36"
                  :height="36"
                  mode="aspectFill"
                  round
                />
                <view>
                  <view class="text-32rpx text-[#333] font-semibold">
                    {{ item.name }}
                  </view>
                  <view class="mt-4rpx text-24rpx text-[#999]">
                    等级 {{ item.level ?? '-' }}
                  </view>
                </view>
              </view>
              <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="item.status" />
            </view>
            <view class="grid grid-cols-3 gap-12rpx rounded-8rpx bg-[#f7f8fa] p-16rpx text-center">
              <view>
                <view class="text-30rpx text-[#333] font-semibold">
                  {{ item.experience ?? 0 }}
                </view>
                <view class="mt-4rpx text-22rpx text-[#999]">
                  升级经验
                </view>
              </view>
              <view>
                <view class="text-30rpx text-[#333] font-semibold">
                  {{ item.discountPercent ?? 0 }}%
                </view>
                <view class="mt-4rpx text-22rpx text-[#999]">
                  享受折扣
                </view>
              </view>
              <view>
                <view class="text-30rpx text-[#333] font-semibold">
                  {{ formatDate(item.createTime) || '-' }}
                </view>
                <view class="mt-4rpx text-22rpx text-[#999]">
                  创建时间
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['member:level:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { MemberLevel } from '@/api/member/level'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getMemberLevelList } from '@/api/member/level'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDate } from '@/utils/date'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const list = ref<MemberLevel[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询等级列表 */
async function queryList() {
  try {
    const data = await getMemberLevelList(queryParams.value)
    pagingRef.value?.completeByTotal(data, data.length)
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

/** 新增等级 */
function handleAdd() {
  uni.navigateTo({
    url: '/pages-member/level/form/index',
  })
}

/** 查看详情 */
function handleDetail(item: MemberLevel) {
  uni.navigateTo({
    url: `/pages-member/level/detail/index?id=${item.id}`,
  })
}

/** 初始化 */
onMounted(() => {
  uni.$on('member:level:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('member:level:reload', reload)
})
</script>
