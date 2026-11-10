<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="我的消息"
      placeholder safe-area-inset-top fixed
    />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" @read-all="handleReadAll" />

    <!-- 消息列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无消息"
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
            <!-- 消息头部 -->
            <view class="mb-16rpx flex items-center justify-between">
              <view class="flex items-center">
                <view
                  v-if="!item.readStatus"
                  class="mr-8rpx h-12rpx w-12rpx flex-shrink-0 rounded-full bg-red-500"
                />
                <view class="text-32rpx text-[#333] font-semibold">
                  {{ item.templateNickname }}
                </view>
              </view>
              <view class="text-26rpx text-[#999]">
                {{ formatDateTime(item.createTime) }}
              </view>
            </view>
            <!-- 消息内容 -->
            <view class="mb-12rpx rounded-8rpx bg-[#f7f8f9] p-20rpx">
              <view class="line-clamp-1 mb-8rpx text-30rpx text-[#323333] font-bold">
                {{ getDictLabel(DICT_TYPE.SYSTEM_NOTIFY_TEMPLATE_TYPE, item.templateType) }}
              </view>
              <view class="line-clamp-2 text-28rpx text-[#777]">
                {{ item.templateContent }}
              </view>
            </view>
            <!-- 底部操作区 -->
            <view class="flex items-center justify-between text-26rpx text-[#999]">
              <view
                v-if="!item.readStatus"
                class="text-[#1890ff]"
                @click.stop="handleReadOne(item)"
              >
                标记已读
              </view>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 详情弹窗 -->
    <DetailPopup ref="detailPopupRef" />
  </view>
</template>

<script lang="ts" setup>
import type { NotifyMessage } from '@/api/system/notify/message'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { ref } from 'vue'
import {
  getMyNotifyMessagePage,
  updateAllNotifyMessageRead,
  updateNotifyMessageRead,
} from '@/api/system/notify/message'
import { getDictLabel } from '@/hooks/useDict'
import { DICT_TYPE } from '@/utils/constants'
import { formatDateTime } from '@/utils/date'
import DetailPopup from './components/detail-popup.vue'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const toast = useToast()
const dialog = useDialog()
const list = ref<NotifyMessage[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数
const detailPopupRef = ref<InstanceType<typeof DetailPopup>>() // 详情弹窗

/** 查询消息列表 */
async function queryList(pageNo: number, pageSize: number) {
  try {
    const params = {
      ...queryParams.value,
      pageNo,
      pageSize,
    }
    const data = await getMyNotifyMessagePage(params)
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

/** 查看详情 */
function handleDetail(item: NotifyMessage) {
  // 如果未读，先标记已读
  if (!item.readStatus) {
    handleReadOne(item, false)
  }
  // 打开详情弹窗
  detailPopupRef.value?.open(item)
}

/** 标记单条已读 */
async function handleReadOne(item: NotifyMessage, showToast = true) {
  await updateNotifyMessageRead(item.id)
  // 更新本地状态
  item.readStatus = true
  item.readTime = new Date()
  if (showToast) {
    toast.success('已标记为已读')
  }
}

/** 标记全部已读 */
async function handleReadAll() {
  try {
    await dialog.confirm({
      title: '提示',
      msg: '确定要将所有消息标记为已读吗？',
    })
  } catch {
    return
  }
  await updateAllNotifyMessageRead()
  toast.success('全部已读成功')
  // 刷新列表
  reload()
}
</script>
