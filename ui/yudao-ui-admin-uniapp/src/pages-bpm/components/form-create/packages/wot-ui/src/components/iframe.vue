<template>
  <wd-form-item :title="rule.title" :title-width="titleWidth" :prop="rule.field" layout="vertical">
    <view class="fc-iframe">
      <template v-if="iframeUrl">
        <!-- #ifdef H5 -->
        <iframe
          :src="iframeUrl"
          :style="{ height: iframeHeight }"
          class="fc-iframe__frame"
          frameborder="0"
        />
        <!-- #endif -->

        <!-- #ifndef H5 -->
        <view class="fc-iframe__card" @click="copyUrl">
          <view class="fc-iframe__url">
            {{ iframeUrl }}
          </view>
          <view class="fc-iframe__action">
            复制网页地址
          </view>
        </view>
        <!-- #endif -->
      </template>

      <view v-else class="fc-iframe__empty">
        未配置网页地址
      </view>
    </view>
  </wd-form-item>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../types/typing'
import { computed } from 'vue'

defineOptions({
  name: 'FcIframe',
})

const props = defineProps<{
  rule: NormalizedFormCreateRule
  titleWidth?: string | number
}>()

const iframeUrl = computed(() => normalizeUrl(
  props.rule.value
  || props.rule.props?.url
  || props.rule.props?.src
  || props.rule.props?.href
  || props.rule.props?.link
  || getChildrenUrl(),
))

const iframeHeight = computed(() => normalizeSize(
  props.rule.props?.height
  || props.rule.props?.iframeHeight
  || props.rule.props?.style?.height
  || '480rpx',
))

function getChildrenUrl() {
  if (!Array.isArray(props.rule.children)) {
    return ''
  }
  return props.rule.children.find(item => typeof item === 'string') || ''
}

function normalizeUrl(value: any) {
  if (!value) {
    return ''
  }
  if (typeof value === 'object') {
    return normalizeUrl(value.url || value.src || value.href)
  }
  return String(value).trim()
}

function normalizeSize(value: string | number) {
  if (typeof value === 'number') {
    return `${value}px`
  }
  return value || '480rpx'
}

function copyUrl() {
  if (!iframeUrl.value) {
    return
  }
  uni.setClipboardData({
    data: iframeUrl.value,
    success: () => {
      uni.showToast({
        icon: 'none',
        title: '网页地址已复制',
      })
    },
  })
}
</script>

<style lang="scss" scoped>
.fc-iframe {
  width: 100%;
}

.fc-iframe__frame {
  display: block;
  width: 100%;
  min-height: 320rpx;
  border: 1px solid #eee;
  border-radius: 8rpx;
  background: #fff;
}

.fc-iframe__card {
  border: 1px solid #eee;
  border-radius: 8rpx;
  background: #fafafa;
  padding: 20rpx 24rpx;
}

.fc-iframe__url {
  color: #333;
  font-size: 26rpx;
  line-height: 38rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fc-iframe__action {
  color: #1677ff;
  font-size: 24rpx;
  line-height: 34rpx;
  margin-top: 12rpx;
}

.fc-iframe__empty {
  color: #999;
  font-size: 26rpx;
  padding: 8rpx 0;
}
</style>
