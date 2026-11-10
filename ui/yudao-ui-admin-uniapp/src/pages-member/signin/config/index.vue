<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="签到配置"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 签到规则列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="20"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无签到配置"
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
            <view class="mb-16rpx flex items-center justify-between">
              <view class="text-32rpx text-[#333] font-semibold">
                第 {{ item.day }} 天
              </view>
              <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="item.status" />
            </view>
            <view class="grid grid-cols-2 gap-12rpx rounded-8rpx bg-[#f7f8fa] p-16rpx text-center">
              <view>
                <view class="text-30rpx text-[#333] font-semibold">
                  {{ item.point ?? 0 }}
                </view>
                <view class="mt-4rpx text-22rpx text-[#999]">
                  奖励积分
                </view>
              </view>
              <view>
                <view class="text-30rpx text-[#333] font-semibold">
                  {{ item.experience ?? 0 }}
                </view>
                <view class="mt-4rpx text-22rpx text-[#999]">
                  奖励经验
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['point:sign-in-config:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { MemberSignInConfig } from '@/api/member/signin/config'
import { onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { getMemberSignInConfigList } from '@/api/member/signin/config'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const list = ref<MemberSignInConfig[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 查询签到配置列表 */
async function queryList() {
  try {
    const data = await getMemberSignInConfigList()
    pagingRef.value?.completeByTotal(data, data.length)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 新增签到配置 */
function handleAdd() {
  uni.navigateTo({
    url: '/pages-member/signin/config/form/index',
  })
}

/** 查看详情 */
function handleDetail(item: MemberSignInConfig) {
  uni.navigateTo({
    url: `/pages-member/signin/config/detail/index?id=${item.id}`,
  })
}

/** 重新加载 */
function reload() {
  pagingRef.value?.reload()
}

/** 初始化 */
onMounted(() => {
  uni.$on('member:signin-config:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('member:signin-config:reload', reload)
})
</script>
