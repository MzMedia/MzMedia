<template>
  <view class="yd-page-container yd-page-container-paging">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="公众号发表记录"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 公众号选择 -->
    <AccountPicker v-model="accountId" @change="handleAccountChange" />

    <!-- 发表列表 -->
    <z-paging
      ref="pagingRef"
      v-model="list"
      :fixed="false"
      class="min-h-0 flex-1"
      :default-page-size="10"
      :refresher-enabled="true"
      :inside-more="true"
      :loading-more-default-as-loading="true"
      empty-view-text="暂无公众号发表记录"
      @query="queryList"
    >
      <view class="p-24rpx">
        <view
          v-for="item in list"
          :key="item.articleId || item.mediaId"
          class="mb-24rpx overflow-hidden rounded-12rpx bg-white shadow-sm"
        >
          <NewsCard :articles="getArticles(item)" @article-click="article => openUrl(article.url)" />
          <view class="p-24rpx">
            <view class="mb-20rpx break-all text-24rpx text-[#999]">
              Article ID：{{ item.articleId || '-' }}
            </view>
            <wd-button
              v-if="hasAccessByCodes(['mp:free-publish:delete'])"
              size="small"
              type="danger"
              block
              @click="handleDelete(item)"
            >
              删除
            </wd-button>
          </view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import type { FreePublish } from '@/api/mp/freePublish'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { ref } from 'vue'
import { deleteFreePublish, getFreePublishPage } from '@/api/mp/freePublish'
import { useAccess } from '@/hooks/useAccess'
import { navigateBackPlus } from '@/utils'
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
const list = ref<FreePublish[]>([]) // 列表数据
const pagingRef = ref<any>() // 分页组件引用

/** 返回上一页 */
function handleBack() {
  navigateBackPlus()
}

/** 获取图文列表 */
function getArticles(item: FreePublish) {
  return item.content?.newsItem || item.articles || []
}

/** 公众号切换 */
function handleAccountChange(id: number) {
  accountId.value = id
  reload()
}

/** 查询发表列表 */
async function queryList(pageNo: number, pageSize: number) {
  if (!accountId.value) {
    pagingRef.value?.completeByTotal([], 0)
    return
  }
  try {
    const data = await getFreePublishPage({
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

/** 删除发表记录 */
async function handleDelete(item: FreePublish) {
  if (!accountId.value || !item.articleId) {
    return
  }
  try {
    await dialog.confirm({
      title: '提示',
      msg: '删除后用户将无法访问此页面，确定删除？',
    })
  } catch {
    return
  }
  await deleteFreePublish(accountId.value, item.articleId)
  toast.success('删除成功')
  reload()
}
</script>
