<!-- 素材选择弹层：按 type（图片 / 语音 / 视频 / 图文）从素材库 / 草稿 / 已发布分页选择素材 -->
<template>
  <wd-popup v-model="innerVisible" position="bottom" safe-area-inset-bottom>
    <view class="h-[80vh] bg-[#f5f5f5]">
      <view class="flex items-center justify-between border-b border-[#f0f0f0] bg-white px-24rpx py-20rpx">
        <view class="text-32rpx text-[#333] font-semibold">
          选择{{ typeLabel }}
        </view>
        <wd-button size="small" variant="plain" @click="innerVisible = false">
          关闭
        </wd-button>
      </view>
      <z-paging
        ref="pagingRef"
        v-model="list"
        :fixed="false"
        class="h-[calc(80vh-96rpx)]"
        :default-page-size="10"
        :refresher-enabled="true"
        :inside-more="true"
        :loading-more-default-as-loading="true"
        empty-view-text="暂无素材数据"
        @query="queryList"
      >
        <view class="p-24rpx">
          <view
            v-for="item in list"
            :key="getItemKey(item)"
            class="mb-24rpx rounded-12rpx bg-white p-24rpx shadow-sm"
          >
            <NewsCard v-if="type === 'news'" :articles="getNewsArticles(item)" />
            <template v-else-if="type === 'image'">
              <wd-img
                v-if="item.url"
                :src="item.url"
                class="mb-16rpx"
                width="100%"
                height="280rpx"
                radius="8rpx"
                mode="aspectFit"
              />
              <view class="text-30rpx text-[#333] font-semibold">
                {{ item.name || item.mediaId || '图片素材' }}
              </view>
            </template>
            <template v-else>
              <view class="mb-12rpx text-30rpx text-[#333] font-semibold">
                {{ item.title || item.name || item.mediaId || typeLabel }}
              </view>
              <view v-if="item.introduction" class="mb-12rpx text-26rpx text-[#666]">
                {{ item.introduction }}
              </view>
              <view class="break-all text-24rpx text-[#999]">
                {{ item.url || '-' }}
              </view>
            </template>
            <wd-button class="mt-20rpx" type="success" size="small" block @click="handleSelect(item)">
              选择
            </wd-button>
          </view>
        </view>
      </z-paging>
    </view>
  </wd-popup>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import { getDraftPage } from '@/api/mp/draft'
import { getFreePublishPage } from '@/api/mp/freePublish'
import { getMaterialPage } from '@/api/mp/material'
import NewsCard from '@/pages-mp/components/news-card.vue'

const props = withDefaults(defineProps<{
  accountId?: number
  type: 'image' | 'voice' | 'video' | 'news'
  visible?: boolean
  newsType?: 'published' | 'draft'
}>(), {
  visible: false,
  newsType: 'published',
})

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'select', item: any): void
}>()

const list = ref<any[]>([]) // 素材列表
const pagingRef = ref<any>() // 分页组件引用

const innerVisible = computed({
  get: () => props.visible,
  set: value => emit('update:visible', value),
})
const typeLabel = computed(() => {
  const labelMap = {
    image: '图片',
    voice: '语音',
    video: '视频',
    news: '图文',
  }
  return labelMap[props.type]
})

watch(
  () => [props.visible, props.accountId, props.type, props.newsType],
  () => {
    if (props.visible) {
      pagingRef.value?.reload()
    }
  },
)

/** 查询素材列表 */
async function queryList(pageNo: number, pageSize: number) {
  if (!props.accountId) {
    pagingRef.value?.completeByTotal([], 0)
    return
  }
  try {
    const params = {
      accountId: props.accountId,
      pageNo,
      pageSize,
    }
    let data: { list: any[], total: number }
    if (props.type === 'news') {
      data = await (props.newsType === 'draft' ? getDraftPage(params) : getFreePublishPage(params))
    } else {
      data = await getMaterialPage({ ...params, type: props.type })
    }
    pagingRef.value?.completeByTotal(data.list || [], data.total || 0)
  } catch {
    pagingRef.value?.complete(false)
  }
}

/** 获取素材主键 */
function getItemKey(item: any) {
  return item.id || item.mediaId || item.articleId
}

/** 获取图文列表 */
function getNewsArticles(item: any) {
  return item?.content?.newsItem || item?.articles || []
}

/** 选择素材 */
function handleSelect(item: any) {
  emit('select', item)
  innerVisible.value = false
}
</script>
