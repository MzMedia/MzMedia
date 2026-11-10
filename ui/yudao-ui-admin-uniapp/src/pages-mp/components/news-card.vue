<!-- 图文消息卡片：展示一组图文 articles（封面图 + 标题） -->
<template>
  <view class="overflow-hidden border border-[#eee] rounded-12rpx bg-white">
    <view
      v-for="(article, index) in safeArticles"
      :key="index"
      class="border-b border-[#f0f0f0] last:border-b-0 active:opacity-70"
      @click="emit('articleClick', article)"
    >
      <view v-if="index === 0" class="relative h-260rpx bg-[#f5f5f5]">
        <wd-img
          v-if="getImageUrl(article)"
          :src="getImageUrl(article)"
          width="100%"
          height="260rpx"
          mode="aspectFill"
        />
        <view v-else class="h-full flex items-center justify-center text-26rpx text-[#999]">
          暂无封面
        </view>
        <view class="absolute bottom-0 left-0 right-0 bg-[rgba(0,0,0,0.55)] px-20rpx py-12rpx text-28rpx text-white">
          {{ article.title || '未命名图文' }}
        </view>
      </view>
      <view v-else class="flex items-center gap-16rpx p-20rpx">
        <view class="min-w-0 flex-1 text-28rpx text-[#333]">
          {{ article.title || '未命名图文' }}
        </view>
        <wd-img
          v-if="getImageUrl(article)"
          :src="getImageUrl(article)"
          width="96rpx"
          height="96rpx"
          radius="8rpx"
          mode="aspectFill"
        />
      </view>
      <view v-if="article.digest" class="px-20rpx pb-16rpx text-24rpx text-[#999]">
        {{ article.digest }}
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { MpArticle } from '@/api/mp/message'
import { computed } from 'vue'

const props = defineProps<{
  articles?: MpArticle[]
}>()

const emit = defineEmits<{
  (e: 'articleClick', article: MpArticle): void
}>()

const safeArticles = computed(() => props.articles || [])

/** 获取图文封面 */
function getImageUrl(article: MpArticle) {
  return article?.picUrl || article?.thumbUrl || article?.thumbMediaUrl || ''
}
</script>
