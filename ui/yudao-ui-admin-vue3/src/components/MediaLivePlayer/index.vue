<template>
  <div class="media-live-player" :class="{ 'is-fullscreen': isFullscreen }" ref="containerRef">
    <!-- 视频渲染区域 -->
    <div class="video-wrapper">
      <video
        ref="videoRef"
        class="video-element"
        autoplay
        muted
        playsinline
        :controls="false"
      ></video>

      <!-- 视频覆盖层 / 状态指示 -->
      <div v-if="loading" class="video-overlay loading-overlay">
        <el-icon class="is-loading" :size="36"><Loading /></el-icon>
        <span class="status-tip">{{ loadingText }}</span>
      </div>

      <div v-else-if="!streamUrl && !playing" class="video-overlay empty-overlay">
        <el-icon :size="48"><VideoCamera /></el-icon>
        <span class="status-tip">{{ emptyText }}</span>
      </div>

      <div v-else-if="error" class="video-overlay error-overlay">
        <el-icon :size="48"><WarningFilled /></el-icon>
        <span class="status-tip">{{ errorMessage }}</span>
        <el-button type="primary" size="small" @click="retryPlay">重试连接</el-button>
      </div>

      <!-- 顶部信息栏 -->
      <div v-if="playing && channelTitle" class="video-header">
        <span class="channel-title">
          <span class="status-dot"></span>
          {{ channelTitle }}
        </span>
        <div class="header-tags">
          <el-tag size="small" effect="dark" type="success">{{ activeProtocol.toUpperCase() }}</el-tag>
          <el-tag v-if="bitrate" size="small" effect="dark" type="info">{{ bitrate }} kbps</el-tag>
        </div>
      </div>

      <!-- 悬浮控制栏 -->
      <div class="video-toolbar" v-if="showControls">
        <div class="toolbar-left">
          <el-button
            circle
            size="small"
            :type="playing ? 'danger' : 'primary'"
            :icon="playing ? VideoPause : VideoPlay"
            @click="togglePlay"
            :title="playing ? '停止播放' : '开始播放'"
          />
          <el-button
            circle
            size="small"
            :icon="isMuted ? MuteNotification : Bell"
            @click="toggleMute"
            :title="isMuted ? '静音' : '开启声音'"
          />
          <el-button
            circle
            size="small"
            :icon="Camera"
            @click="takeSnapshot"
            title="截图抓拍"
          />
          <el-button
            v-if="hasPtz"
            circle
            size="small"
            :type="showPtzPanel ? 'primary' : 'default'"
            :icon="Compass"
            @click="showPtzPanel = !showPtzPanel"
            title="云台控制"
          />
        </div>

        <div class="toolbar-right">
          <!-- 协议/流类型切换 -->
          <el-dropdown trigger="click" @command="handleProtocolChange" v-if="streamSources">
            <span class="protocol-dropdown-btn">
              {{ activeProtocol.toUpperCase() }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="rtc" :disabled="!streamSources.rtc">WebRTC (低延迟)</el-dropdown-item>
                <el-dropdown-item command="flv" :disabled="!streamSources.flv && !streamSources.ws_flv">HTTP/WS-FLV</el-dropdown-item>
                <el-dropdown-item command="hls" :disabled="!streamSources.hls">HLS (m3u8)</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <el-button
            circle
            size="small"
            :icon="Refresh"
            @click="refreshStream"
            title="刷新视频流"
          />
          <el-button
            circle
            size="small"
            :icon="FullScreen"
            @click="toggleFullscreen"
            title="全屏切换"
          />
        </div>
      </div>

      <!-- 嵌入式云台控制器面板 -->
      <transition name="el-zoom-in-bottom">
        <div v-if="showPtzPanel && hasPtz" class="ptz-overlay-panel">
          <div class="ptz-panel-header">
            <span>云台与镜头控制</span>
            <el-icon class="close-icon" @click="showPtzPanel = false"><Close /></el-icon>
          </div>
          <div class="ptz-panel-body">
            <!-- 八向控制轮盘 -->
            <div class="ptz-wheel">
              <div class="ptz-row">
                <el-button class="ptz-btn" @mousedown="onPtzStart(8)" @mouseup="onPtzEnd" title="左上">↖</el-button>
                <el-button class="ptz-btn" @mousedown="onPtzStart(4)" @mouseup="onPtzEnd" title="向上">▲</el-button>
                <el-button class="ptz-btn" @mousedown="onPtzStart(7)" @mouseup="onPtzEnd" title="右上">↗</el-button>
              </div>
              <div class="ptz-row">
                <el-button class="ptz-btn" @mousedown="onPtzStart(2)" @mouseup="onPtzEnd" title="向左">◀</el-button>
                <el-button class="ptz-btn ptz-center" @click="onPtzStop" title="复位/停止">●</el-button>
                <el-button class="ptz-btn" @mousedown="onPtzStart(1)" @mouseup="onPtzEnd" title="向右">▶</el-button>
              </div>
              <div class="ptz-row">
                <el-button class="ptz-btn" @mousedown="onPtzStart(6)" @mouseup="onPtzEnd" title="左下">↙</el-button>
                <el-button class="ptz-btn" @mousedown="onPtzStart(3)" @mouseup="onPtzEnd" title="向下">▼</el-button>
                <el-button class="ptz-btn" @mousedown="onPtzStart(5)" @mouseup="onPtzEnd" title="右下">↘</el-button>
              </div>
            </div>

            <!-- 镜头操作 (变倍/变焦/光圈) -->
            <div class="ptz-lens-actions">
              <div class="lens-item">
                <span class="lens-label">变倍:</span>
                <el-button-group size="small">
                  <el-button @mousedown="onPtzStart(9)" @mouseup="onPtzEnd" title="放大">+</el-button>
                  <el-button @mousedown="onPtzStart(10)" @mouseup="onPtzEnd" title="缩小">-</el-button>
                </el-button-group>
              </div>
              <div class="lens-item">
                <span class="lens-label">聚焦:</span>
                <el-button-group size="small">
                  <el-button @mousedown="onFocusStart(1)" @mouseup="onFocusEnd" title="远焦">+</el-button>
                  <el-button @mousedown="onFocusStart(2)" @mouseup="onFocusEnd" title="近焦">-</el-button>
                </el-button-group>
              </div>
              <div class="lens-item">
                <span class="lens-label">光圈:</span>
                <el-button-group size="small">
                  <el-button @mousedown="onIrisStart(1)" @mouseup="onIrisEnd" title="光圈开">+</el-button>
                  <el-button @mousedown="onIrisStart(2)" @mouseup="onIrisEnd" title="光圈关">-</el-button>
                </el-button-group>
              </div>
            </div>

            <!-- 云台速度滑块 -->
            <div class="ptz-speed-slider">
              <span class="slider-label">转速: {{ ptzSpeed }}</span>
              <el-slider v-model="ptzSpeed" :min="1" :max="255" size="small" />
            </div>

            <!-- 预置位快捷操作 -->
            <div class="ptz-presets">
              <div class="preset-header">
                <span>预置位</span>
                <el-button link type="primary" size="small" @click="saveCurrentPreset">添加当前</el-button>
              </div>
              <div class="preset-list">
                <el-tag
                  v-for="item in presetList"
                  :key="item.presetId"
                  class="preset-tag"
                  effect="plain"
                  size="small"
                  @click="callPresetIndex(item.presetId)"
                >
                  {{ item.presetName || `预置位 ${item.presetId}` }}
                </el-tag>
                <span v-if="presetList.length === 0" class="no-presets">暂无预置位</span>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import {
  VideoPlay,
  VideoPause,
  FullScreen,
  Refresh,
  Camera,
  Close,
  Compass,
  Bell,
  MuteNotification,
  ArrowDown,
  Loading,
  VideoCamera,
  WarningFilled
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { ptzControl, irisControl, focusControl, callPreset, addPreset } from '@/api/media/channel'

const props = defineProps({
  channelTitle: {
    type: String,
    default: ''
  },
  deviceId: {
    type: String,
    default: ''
  },
  channelId: {
    type: String,
    default: ''
  },
  streamSources: {
    type: Object as () => Record<string, any> | null,
    default: null
  },
  hasPtz: {
    type: Boolean,
    default: true
  },
  autoPlay: {
    type: Boolean,
    default: true
  },
  showControls: {
    type: Boolean,
    default: true
  },
  emptyText: {
    type: String,
    default: '请选择通道开启视频预览'
  }
})

const emit = defineEmits(['play', 'stop', 'snapshot', 'error'])

const containerRef = ref<HTMLDivElement>()
const videoRef = ref<HTMLVideoElement>()
const playing = ref(false)
const loading = ref(false)
const loadingText = ref('视频流加载中...')
const error = ref(false)
const errorMessage = ref('')
const isMuted = ref(true)
const isFullscreen = ref(false)
const showPtzPanel = ref(false)
const ptzSpeed = ref(80)
const activeProtocol = ref<'rtc' | 'flv' | 'hls'>('rtc')
const streamUrl = ref('')
const bitrate = ref<number>(0)
const presetList = ref<any[]>([
  { presetId: 1, presetName: '正门全景' },
  { presetId: 2, presetName: '车牌特写' },
  { presetId: 3, presetName: '巡更点A' }
])

// 初始化或更新播放地址
const initPlay = () => {
  if (!props.streamSources) {
    playing.value = false
    streamUrl.value = ''
    return
  }

  loading.value = true
  loadingText.value = '正在拉取实时视频流...'
  error.value = false

  const src = props.streamSources
  if (activeProtocol.value === 'rtc' && src.rtc) {
    streamUrl.value = src.rtc
  } else if (activeProtocol.value === 'flv' && (src.flv || src.ws_flv)) {
    streamUrl.value = src.flv || src.ws_flv
  } else if (activeProtocol.value === 'hls' && src.hls) {
    streamUrl.value = src.hls
  } else {
    // 自动选择可用流
    streamUrl.value = src.rtc || src.flv || src.ws_flv || src.hls || src.rtmp || ''
  }

  if (videoRef.value && streamUrl.value) {
    videoRef.value.src = streamUrl.value
    videoRef.value
      .play()
      .then(() => {
        loading.value = false
        playing.value = true
        emit('play', streamUrl.value)
      })
      .catch(() => {
        loading.value = false
        // 允许原生静音后播放
        if (videoRef.value) {
          videoRef.value.muted = true
          videoRef.value.play().then(() => {
            playing.value = true
          }).catch(() => {
            // 播放器流地址挂载
            playing.value = true
          })
        }
      })
  } else {
    loading.value = false
  }
}

watch(
  () => props.streamSources,
  () => {
    initPlay()
  },
  { deep: true }
)

const togglePlay = () => {
  if (playing.value) {
    if (videoRef.value) {
      videoRef.value.pause()
      videoRef.value.src = ''
    }
    playing.value = false
    emit('stop')
  } else {
    initPlay()
  }
}

const toggleMute = () => {
  if (!videoRef.value) return
  isMuted.value = !isMuted.value
  videoRef.value.muted = isMuted.value
}

const toggleFullscreen = () => {
  if (!containerRef.value) return
  if (!document.fullscreenElement) {
    containerRef.value.requestFullscreen().then(() => {
      isFullscreen.value = true
    })
  } else {
    document.exitFullscreen().then(() => {
      isFullscreen.value = false
    })
  }
}

const refreshStream = () => {
  initPlay()
}

const retryPlay = () => {
  error.value = false
  initPlay()
}

const handleProtocolChange = (cmd: string) => {
  activeProtocol.value = cmd as any
  initPlay()
}

// 视频抓图
const takeSnapshot = () => {
  if (!videoRef.value) return
  const video = videoRef.value
  const canvas = document.createElement('canvas')
  canvas.width = video.videoWidth || 1280
  canvas.height = video.videoHeight || 720
  const ctx = canvas.getContext('2d')
  if (ctx) {
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
    const base64 = canvas.toDataURL('image/jpeg', 0.92)
    const link = document.createElement('a')
    link.download = `snapshot_${props.channelTitle || 'channel'}_${Date.now()}.jpg`
    link.href = base64
    link.click()
    ElMessage.success('抓图成功并已下载')
    emit('snapshot', base64)
  }
}

// 云台控制
const onPtzStart = (cmd: number) => {
  if (!props.deviceId || !props.channelId) return
  ptzControl(props.deviceId, props.channelId, cmd, ptzSpeed.value, ptzSpeed.value, ptzSpeed.value)
}

const onPtzEnd = () => {
  onPtzStop()
}

const onPtzStop = () => {
  if (!props.deviceId || !props.channelId) return
  ptzControl(props.deviceId, props.channelId, 0, 0, 0, 0)
}

// 变倍与光圈控制
const onFocusStart = (cmd: number) => {
  if (!props.deviceId || !props.channelId) return
  focusControl(props.deviceId, props.channelId, cmd, ptzSpeed.value)
}

const onFocusEnd = () => {
  if (!props.deviceId || !props.channelId) return
  focusControl(props.deviceId, props.channelId, 0, 0)
}

const onIrisStart = (cmd: number) => {
  if (!props.deviceId || !props.channelId) return
  irisControl(props.deviceId, props.channelId, cmd, ptzSpeed.value)
}

const onIrisEnd = () => {
  if (!props.deviceId || !props.channelId) return
  irisControl(props.deviceId, props.channelId, 0, 0)
}

const callPresetIndex = (id: number) => {
  if (!props.deviceId || !props.channelId) return
  callPreset(props.deviceId, props.channelId, id).then(() => {
    ElMessage.success(`调用预置位 [${id}] 成功`)
  })
}

const saveCurrentPreset = () => {
  const nextId = presetList.value.length + 1
  if (!props.deviceId || !props.channelId) {
    presetList.value.push({ presetId: nextId, presetName: `预置位 ${nextId}` })
    ElMessage.success(`添加预置位 ${nextId}`)
    return
  }
  addPreset(props.deviceId, props.channelId, `预置位 ${nextId}`, nextId).then(() => {
    presetList.value.push({ presetId: nextId, presetName: `预置位 ${nextId}` })
    ElMessage.success(`添加预置位 ${nextId} 成功`)
  })
}

onMounted(() => {
  if (props.autoPlay && props.streamSources) {
    initPlay()
  }
})

onBeforeUnmount(() => {
  if (videoRef.value) {
    videoRef.value.pause()
    videoRef.value.src = ''
  }
})
</script>

<style scoped lang="scss">
.media-live-player {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 240px;
  background-color: #0d1117;
  border-radius: 6px;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  &.is-fullscreen {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    z-index: 9999;
    border-radius: 0;
  }

  .video-wrapper {
    position: relative;
    width: 100%;
    height: 100%;
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #000;
  }

  .video-element {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }

  .video-overlay {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #8b949e;
    gap: 12px;
    background: rgba(0, 0, 0, 0.65);

    .status-tip {
      font-size: 13px;
    }
  }

  .video-header {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 12px;
    background: linear-gradient(180deg, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0) 100%);
    color: #f0f6fc;
    font-size: 13px;
    z-index: 2;

    .channel-title {
      display: flex;
      align-items: center;
      gap: 6px;

      .status-dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background-color: #3fb950;
      }
    }

    .header-tags {
      display: flex;
      gap: 6px;
    }
  }

  .video-toolbar {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 42px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 12px;
    background: linear-gradient(0deg, rgba(0, 0, 0, 0.75) 0%, rgba(0, 0, 0, 0) 100%);
    opacity: 0;
    transition: opacity 0.25s ease;
    z-index: 3;

    .toolbar-left,
    .toolbar-right {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .protocol-dropdown-btn {
      color: #f0f6fc;
      font-size: 12px;
      cursor: pointer;
      display: flex;
      align-items: center;
      padding: 2px 6px;
      background: rgba(255, 255, 255, 0.15);
      border-radius: 4px;
    }
  }

  &:hover .video-toolbar {
    opacity: 1;
  }

  .ptz-overlay-panel {
    position: absolute;
    right: 12px;
    bottom: 50px;
    width: 220px;
    background: rgba(18, 22, 28, 0.92);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 8px;
    padding: 10px;
    color: #e6edf3;
    z-index: 4;

    .ptz-panel-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 12px;
      font-weight: 600;
      padding-bottom: 6px;
      border-bottom: 1px solid rgba(255, 255, 255, 0.08);

      .close-icon {
        cursor: pointer;
        &:hover {
          color: #f85149;
        }
      }
    }

    .ptz-panel-body {
      padding-top: 8px;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .ptz-wheel {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 3px;

      .ptz-row {
        display: flex;
        gap: 3px;
      }

      .ptz-btn {
        width: 32px;
        height: 28px;
        padding: 0;
        font-size: 12px;
        background: rgba(255, 255, 255, 0.08);
        border: 1px solid rgba(255, 255, 255, 0.15);
        color: #f0f6fc;

        &:hover {
          background: var(--el-color-primary);
          border-color: var(--el-color-primary);
        }

        &.ptz-center {
          background: rgba(248, 81, 73, 0.2);
          border-color: #f85149;
          color: #f85149;
        }
      }
    }

    .ptz-lens-actions {
      display: flex;
      flex-direction: column;
      gap: 4px;
      font-size: 11px;

      .lens-item {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .lens-label {
          color: #8b949e;
        }
      }
    }

    .ptz-speed-slider {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 11px;

      .slider-label {
        color: #8b949e;
        white-space: nowrap;
      }
    }

    .ptz-presets {
      display: flex;
      flex-direction: column;
      gap: 4px;
      font-size: 11px;

      .preset-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        color: #8b949e;
      }

      .preset-list {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
        max-height: 60px;
        overflow-y: auto;

        .preset-tag {
          cursor: pointer;
          &:hover {
            border-color: var(--el-color-primary);
            color: var(--el-color-primary);
          }
        }

        .no-presets {
          color: #6e7681;
          font-size: 10px;
        }
      }
    }
  }
}
</style>
