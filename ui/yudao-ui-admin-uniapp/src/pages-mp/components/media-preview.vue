<!-- 公众号音视频预览：视频内联播放；语音 InnerAudioContext 播放，amr 等格式降级复制链接 -->
<template>
  <view>
    <template v-if="isVideo">
      <video v-if="url" :src="url" class="w-full rounded-12rpx" style="height: 360rpx" object-fit="contain" />
      <text v-else class="text-26rpx text-[#999]">暂无视频</text>
    </template>
    <template v-else>
      <view
        v-if="url"
        class="inline-flex items-center rounded-full bg-[#f5f5f5] px-24rpx py-12rpx active:opacity-70"
        @click="handleVoiceTap"
      >
        <text class="text-26rpx" :class="playable ? 'text-[#576b95]' : 'text-[#999]'">
          {{ voiceLabel }}
        </text>
      </view>
      <text v-else class="text-26rpx text-[#999]">暂无语音</text>
    </template>
  </view>
</template>

<script lang="ts" setup>
import { computed, onUnmounted, ref } from 'vue'

const props = defineProps<{
  type?: string
  url?: string
  format?: string
}>()

const playing = ref(false) // 语音播放状态
let audioContext: any // 语音播放上下文

const isVideo = computed(() => props.type === 'video' || props.type === 'shortvideo')
// amr 等格式 InnerAudioContext 多端不支持解码，降级为复制链接
const playable = computed(() => {
  if (!props.url) {
    return false
  }
  const fmt = (props.format || props.url.split('.').pop() || '').toLowerCase()
  return fmt !== 'amr'
})
const voiceLabel = computed(() => {
  if (!playable.value) {
    return '语音（amr）· 点击复制链接'
  }
  return playing.value ? '⏸ 暂停' : '▶ 播放语音'
})

/** 点击语音 */
function handleVoiceTap() {
  if (!playable.value) {
    copyUrl()
    return
  }
  togglePlay()
}

/** 播放 / 暂停语音 */
function togglePlay() {
  if (!props.url) {
    return
  }
  if (!audioContext) {
    audioContext = uni.createInnerAudioContext()
    audioContext.onPlay(() => (playing.value = true))
    audioContext.onPause(() => (playing.value = false))
    audioContext.onStop(() => (playing.value = false))
    audioContext.onEnded(() => (playing.value = false))
    audioContext.onError(() => {
      playing.value = false
      uni.showToast({ title: '播放失败', icon: 'none' })
    })
  }
  if (playing.value) {
    audioContext.pause()
    return
  }
  audioContext.src = props.url
  audioContext.play()
}

/** 复制链接 */
function copyUrl() {
  if (!props.url) {
    return
  }
  uni.setClipboardData({
    data: props.url,
    success: () => uni.showToast({ title: '链接已复制', icon: 'none' }),
  })
}

onUnmounted(() => {
  audioContext?.destroy()
})
</script>
