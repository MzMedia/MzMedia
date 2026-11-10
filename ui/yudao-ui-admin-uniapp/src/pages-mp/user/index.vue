<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="公众号粉丝"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 公众号选择 -->
    <AccountPicker v-model="accountId" @change="handleAccountChange" />

    <!-- 搜索组件 -->
    <SearchForm @search="handleQuery" @reset="handleReset" />

    <!-- 粉丝列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无公众号粉丝数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.id"
          class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm"
          @click="handleDetail(item)"
        >
          <view class="mb-16rpx flex items-center gap-16rpx">
            <wd-img v-if="item.headImageUrl" :src="item.headImageUrl" width="80rpx" height="80rpx" mode="aspectFill" round />
            <view
              v-else
              class="h-80rpx w-80rpx flex items-center justify-center rounded-full bg-[#1890ff] text-32rpx text-white"
            >
              {{ (item.nickname || '粉')?.charAt(0) }}
            </view>
            <view class="min-w-0 flex-1">
              <view class="truncate text-32rpx text-[#333] font-semibold">
                {{ item.nickname || '微信用户' }}
              </view>
              <view class="truncate text-24rpx text-[#999]">
                {{ item.openid || '-' }}
              </view>
            </view>
            <wd-tag :type="item.subscribeStatus === 0 ? 'success' : 'danger'" plain>
              {{ item.subscribeStatus === 0 ? '已订阅' : '未订阅' }}
            </wd-tag>
          </view>
          <view class="mb-12rpx flex flex-wrap gap-8rpx">
            <wd-tag v-for="tagId in item.tagIds || []" :key="tagId" plain>
              {{ getTagName(tagId) }}
            </wd-tag>
          </view>
          <view class="text-24rpx text-[#999]">
            订阅时间：{{ formatDateTime(item.subscribeTime) || '-' }}
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 同步按钮 -->
    <view
      v-if="hasAccessByCodes(['mp:user:sync'])"
      class="fixed bottom-60rpx right-32rpx z-10"
    >
      <wd-button type="success" size="small" :loading="syncing" @click="handleSync">
        同步
      </wd-button>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { MpUser } from '@/api/mp/user'
import type { Tag } from '@/api/mp/tag'
import { onUnload } from '@dcloudio/uni-app'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onMounted, ref } from 'vue'
import { getSimpleTagList } from '@/api/mp/tag'
import { getUserPage, syncUser } from '@/api/mp/user'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { formatDateTime } from '@/utils/date'
import AccountPicker from '@/pages-mp/account/components/account-picker.vue'
import SearchForm from './components/search-form.vue'

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const toast = useToast()
const dialog = useDialog()
const accountId = ref<number>() // 当前公众号编号
const list = ref<MpUser[]>([]) // 列表数据
const tagList = ref<Tag[]>([]) // 标签列表
const pagingRef = ref<any>() // 分页组件引用
const queryParams = ref<Record<string, any>>({}) // 查询参数
const syncing = ref(false) // 同步状态

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 获取标签名称 */
function getTagName(tagId: number) {
  const tag = tagList.value.find(item => item.tagId === tagId)
  return tag?.name || tagId
}

/** 公众号切换 */
function handleAccountChange(id: number) {
  accountId.value = id
  reload()
}

/** 查询粉丝列表 */
async function queryList(pageNo: number, pageSize: number) {
  if (!accountId.value) {
    pagingRef.value?.completeByTotal([], 0)
    return
  }
  try {
    const data = await getUserPage({
      ...queryParams.value,
      accountId: accountId.value,
      pageNo,
      pageSize,
    })
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

/** 加载标签列表 */
async function loadTagList() {
  try {
    tagList.value = await getSimpleTagList()
  } catch {
    // 标签只是辅助展示，失败时保留编号
    tagList.value = []
  }
}

/** 查看详情 */
function handleDetail(item: MpUser) {
  uni.navigateTo({ url: `/pages-mp/user/detail/index?id=${item.id}` })
}

/** 同步粉丝 */
async function handleSync() {
  if (!accountId.value) {
    toast.show('请先选择公众号')
    return
  }
  try {
    await dialog.confirm({
      title: '提示',
      msg: '确定同步公众号粉丝吗？同步需要一段时间，建议稍后再查询。',
    })
  } catch {
    return
  }
  syncing.value = true
  try {
    await syncUser(accountId.value)
    toast.success('开始同步粉丝')
    reload()
  } finally {
    syncing.value = false
  }
}

/** 初始化 */
onMounted(() => {
  loadTagList()
  uni.$on('mp:user:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('mp:user:reload', reload)
})
</script>
