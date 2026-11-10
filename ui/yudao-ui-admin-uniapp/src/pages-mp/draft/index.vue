<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="公众号草稿"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 公众号选择 -->
    <AccountPicker v-model="accountId" @change="handleAccountChange" />

    <!-- 草稿列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无公众号草稿数据"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.mediaId"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white shadow-sm"
        >
          <NewsCard :articles="getArticles(item)" @article-click="article => openUrl(article.url)" />
          <view class="p-24rpx">
            <view class="mb-12rpx text-24rpx text-[#999]">
              更新时间：{{ item.updateTime ? formatDateTime(item.updateTime * 1000) : '-' }}
            </view>
            <view class="mb-20rpx break-all text-24rpx text-[#999]">
              Media ID：{{ item.mediaId || '-' }}
            </view>
            <view class="flex gap-16rpx">
              <wd-button
                v-if="hasAccessByCodes(['mp:draft:update'])"
                class="flex-1" size="small" type="warning" @click="handleEdit(item)"
              >
                编辑
              </wd-button>
              <wd-button
                v-if="hasAccessByCodes(['mp:free-publish:submit'])"
                class="flex-1" size="small" type="primary" @click="handlePublish(item)"
              >
                发布
              </wd-button>
              <wd-button
                v-if="hasAccessByCodes(['mp:draft:delete'])"
                class="flex-1" size="small" type="danger" @click="handleDelete(item)"
              >
                删除
              </wd-button>
            </view>
          </view>
        </view>
      </view>
    </z-paging>

    <!-- 新增按钮 -->
    <wd-fab
      v-if="hasAccessByCodes(['mp:draft:create'])"
      position="right-bottom"
      type="primary"
      :expandable="false"
      @click="handleAdd"
    />
  </view>
</template>

<script lang="ts" setup>
import type { Draft } from '@/api/mp/draft'
import { onUnload } from '@dcloudio/uni-app'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onMounted, ref } from 'vue'
import { deleteDraft, getDraftPage } from '@/api/mp/draft'
import { submitFreePublish } from '@/api/mp/freePublish'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
import { formatDateTime } from '@/utils/date'
import { openUrl } from '@/utils/url'
import AccountPicker from '@/pages-mp/account/components/account-picker.vue'
import NewsCard from '../components/news-card.vue'

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
const list = ref<Draft[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 获取图文列表 */
function getArticles(item: Draft) {
  return item.content?.newsItem || item.articles || []
}

/** 公众号切换 */
function handleAccountChange(id: number) {
  accountId.value = id
  reload()
}

/** 查询草稿列表 */
async function queryList(pageNo: number, pageSize: number) {
  if (!accountId.value) {
    pagingRef.value?.completeByTotal([], 0)
    return
  }
  try {
    const data = await getDraftPage({
      accountId: accountId.value,
      pageNo,
      pageSize,
    })
    pagingRef.value?.completeByTotal(data.list, data.total)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 重新加载 */
function reload() {
  pagingRef.value?.reload()
}

/** 新增草稿 */
function handleAdd() {
  if (!accountId.value) {
    toast.show('请先选择公众号')
    return
  }
  uni.navigateTo({ url: `/pages-mp/draft/form/index?accountId=${accountId.value}` })
}

/** 编辑草稿 */
function handleEdit(item: Draft) {
  if (!accountId.value || !item.mediaId) {
    return
  }
  uni.setStorageSync('mp:draft:edit', item)
  uni.navigateTo({ url: `/pages-mp/draft/form/index?accountId=${accountId.value}&mediaId=${item.mediaId}` })
}

/** 发布草稿 */
async function handlePublish(item: Draft) {
  if (!accountId.value || !item.mediaId) {
    return
  }
  try {
    await dialog.confirm({
      title: '提示',
      msg: '确定发布该公众号草稿吗？',
    })
  } catch {
    return
  }
  await submitFreePublish(accountId.value, item.mediaId)
  toast.success('发布成功')
  reload()
}

/** 删除草稿 */
async function handleDelete(item: Draft) {
  if (!accountId.value || !item.mediaId) {
    return
  }
  try {
    await dialog.confirm({
      title: '提示',
      msg: '确定删除该公众号草稿吗？',
    })
  } catch {
    return
  }
  await deleteDraft(accountId.value, item.mediaId)
  toast.success('删除成功')
  reload()
}

/** 初始化 */
onMounted(() => {
  uni.$on('mp:draft:reload', reload)
})

/** 卸载 */
onUnload(() => {
  uni.$off('mp:draft:reload', reload)
})
</script>
