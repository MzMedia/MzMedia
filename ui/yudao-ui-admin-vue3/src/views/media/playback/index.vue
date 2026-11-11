<template>
  <div class="media-playback-container">
    <!-- 左侧选择栏：通道与日期 -->
    <div class="playback-sidebar">
      <div class="sidebar-title">录像通道选择</div>
      <el-select
        v-model="selectedChannelId"
        placeholder="请选择通道"
        filterable
        @change="onChannelChange"
        style="width: 100%; margin-bottom: 12px"
      >
        <el-option
          v-for="ch in channelList"
          :key="ch.id"
          :label="ch.name || ch.channelId"
          :value="ch.id"
        />
      </el-select>

      <div class="sidebar-title">录像日期</div>
      <el-date-picker
        v-model="selectedDate"
        type="date"
        placeholder="选择录像日期"
        value-format="YYYY-MM-DD"
        :disabled-date="disableFutureDates"
        @change="onDateChange"
        style="width: 100%; margin-bottom: 16px"
      />

      <div class="sidebar-title">录像文件片段 ({{ recordFiles.length }})</div>
      <div class="file-list-scroll">
        <el-scrollbar>
          <div
            v-for="file in recordFiles"
            :key="file.id"
            class="record-file-card"
            :class="{ 'is-active': activeFile?.id === file.id }"
            @click="playRecordFile(file)"
          >
            <div class="file-time-span">
              <el-icon><VideoPlay /></el-icon>
              <span>{{ formatTimeOnly(file.startDate) }} - {{ formatTimeOnly(file.endDate) }}</span>
            </div>
            <div class="file-meta">
              <span class="file-size">{{ formatFileSize(file.fileSize) }}</span>
              <el-button link type="primary" size="small" :icon="Download" @click.stop="downloadFile(file)">
                下载
              </el-button>
            </div>
          </div>

          <el-empty
            v-if="recordFiles.length === 0"
            description="当日无录像记录"
            :image-size="60"
          />
        </el-scrollbar>
      </div>
    </div>

    <!-- 右侧主体：播放区与 24h 时间轴 -->
    <div class="playback-main">
      <!-- 播放视窗 -->
      <div class="player-wrapper">
        <video
          ref="videoEl"
          class="playback-video"
          controls
          autoplay
        ></video>
        <div v-if="!activePlayUrl" class="player-placeholder">
          <el-icon :size="48"><Film /></el-icon>
          <span>请在左侧选择通道与录像片段进行回放</span>
        </div>
      </div>

      <!-- 24小时交互时间轴 -->
      <div class="timeline-bar-wrapper">
        <div class="timeline-scale">
          <span v-for="h in 25" :key="h" class="scale-hour">{{ String(h - 1).padStart(2, '0') }}:00</span>
        </div>
        <div class="timeline-track" @click="handleTimelineClick" ref="timelineRef">
          <!-- 录像片段区间 (绿色高亮) -->
          <div
            v-for="seg in timelineSegments"
            :key="seg.id"
            class="timeline-segment"
            :style="{ left: `${seg.leftPercent}%`, width: `${seg.widthPercent}%` }"
            :title="`${seg.start} ~ ${seg.end}`"
          ></div>

          <!-- 当前播放游标 -->
          <div
            class="timeline-cursor"
            :style="{ left: `${cursorPercent}%` }"
          >
            <div class="cursor-tag">{{ currentCursorTime }}</div>
          </div>
        </div>
      </div>

      <!-- 回放控制台 -->
      <div class="playback-controls">
        <div class="controls-left">
          <el-button-group size="small">
            <el-button :icon="isPlaying ? VideoPause : VideoPlay" type="primary" @click="togglePlay">
              {{ isPlaying ? '暂停' : '播放' }}
            </el-button>
            <el-button :icon="RefreshLeft" @click="seekDelta(-30)">快退30s</el-button>
            <el-button :icon="RefreshRight" @click="seekDelta(30)">快进30s</el-button>
          </el-button-group>
        </div>

        <div class="controls-right">
          <span class="speed-label">倍速:</span>
          <el-radio-group v-model="playbackSpeed" size="small" @change="handleSpeedChange">
            <el-radio-button :value="0.5">0.5x</el-radio-button>
            <el-radio-button :value="1.0">1.0x</el-radio-button>
            <el-radio-button :value="2.0">2.0x</el-radio-button>
            <el-radio-button :value="4.0">4.0x</el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { VideoPlay, VideoPause, RefreshLeft, RefreshRight, Download, Film } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { getChannelPage, MediaChannelVO } from '@/api/media/channel'
import { getCloudRecordList, getRecordPlayPath, CloudRecordFileVO } from '@/api/media/record'

const channelList = ref<MediaChannelVO[]>([])
const selectedChannelId = ref<number>()
const selectedDate = ref(dayjs().format('YYYY-MM-DD'))
const recordFiles = ref<CloudRecordFileVO[]>([])
const activeFile = ref<CloudRecordFileVO | null>(null)
const activePlayUrl = ref('')
const isPlaying = ref(false)
const playbackSpeed = ref(1.0)
const cursorPercent = ref(0)
const videoEl = ref<HTMLVideoElement>()
const timelineRef = ref<HTMLDivElement>()

const disableFutureDates = (date: Date) => {
  return date.getTime() > Date.now()
}

const formatTimeOnly = (dateTimeStr: string) => {
  if (!dateTimeStr) return ''
  return dayjs(dateTimeStr).format('HH:mm:ss')
}

const formatFileSize = (bytes: number) => {
  if (!bytes) return '0 B'
  const mb = bytes / (1024 * 1024)
  return `${mb.toFixed(1)} MB`
}

const currentCursorTime = computed(() => {
  const totalSeconds = (cursorPercent.value / 100) * 86400
  const h = Math.floor(totalSeconds / 3600)
  const m = Math.floor((totalSeconds % 3600) / 60)
  const s = Math.floor(totalSeconds % 60)
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
})

// 计算在 24h 时间轴中的绿色百分比区间
const timelineSegments = computed(() => {
  return recordFiles.value.map((f) => {
    const startSec = dayjs(f.startDate).hour() * 3600 + dayjs(f.startDate).minute() * 60 + dayjs(f.startDate).second()
    const endSec = dayjs(f.endDate).hour() * 3600 + dayjs(f.endDate).minute() * 60 + dayjs(f.endDate).second()
    const leftPercent = (startSec / 86400) * 100
    const widthPercent = Math.max(((endSec - startSec) / 86400) * 100, 0.4)
    return {
      id: f.id,
      start: formatTimeOnly(f.startDate),
      end: formatTimeOnly(f.endDate),
      leftPercent,
      widthPercent
    }
  })
})

const onChannelChange = () => {
  loadRecords()
}

const onDateChange = () => {
  loadRecords()
}

const loadRecords = async () => {
  if (!selectedChannelId.value || !selectedDate.value) return
  const channel = channelList.value.find((c) => c.id === selectedChannelId.value)
  if (!channel) return

  try {
    const res = await getCloudRecordList({
      app: 'live',
      stream: channel.streamId || channel.channelId,
      date: selectedDate.value
    })
    recordFiles.value = res?.data || []
  } catch (e: any) {
    recordFiles.value = []
  }
}

const playRecordFile = async (file: CloudRecordFileVO) => {
  activeFile.value = file
  try {
    const res = await getRecordPlayPath(file.id)
    if (res && res.data) {
      activePlayUrl.value = res.data
      if (videoEl.value) {
        videoEl.value.src = res.data
        videoEl.value.playbackRate = playbackSpeed.value
        videoEl.value.play().then(() => {
          isPlaying.value = true
        })
      }
    }
  } catch (err: any) {
    ElMessage.error(err.message || '获取录像播放地址失败')
  }
}

const togglePlay = () => {
  if (!videoEl.value) return
  if (videoEl.value.paused) {
    videoEl.value.play()
    isPlaying.value = true
  } else {
    videoEl.value.pause()
    isPlaying.value = false
  }
}

const seekDelta = (seconds: number) => {
  if (!videoEl.value) return
  videoEl.value.currentTime += seconds
}

const handleSpeedChange = (speed: any) => {
  if (!videoEl.value) return
  videoEl.value.playbackRate = Number(speed)
}

const handleTimelineClick = (e: MouseEvent) => {
  if (!timelineRef.value) return
  const rect = timelineRef.value.getBoundingClientRect()
  const clickX = e.clientX - rect.left
  const percent = Math.max(0, Math.min(100, (clickX / rect.width) * 100))
  cursorPercent.value = percent
}

const downloadFile = (file: CloudRecordFileVO) => {
  if (file.filePath) {
    window.open(file.filePath, '_blank')
  } else {
    ElMessage.info('正在请求下载任务')
  }
}

onMounted(async () => {
  const res = await getChannelPage({ page: 1, count: 100 })
  channelList.value = res?.data?.list || res?.data || []
  if (channelList.value.length > 0) {
    selectedChannelId.value = channelList.value[0].id
    loadRecords()
  }
})
</script>

<style scoped lang="scss">
.media-playback-container {
  display: flex;
  height: calc(100vh - 120px);
  gap: 10px;
  padding: 10px;
  background-color: #f0f2f5;

  .playback-sidebar {
    width: 300px;
    background: #fff;
    border-radius: 6px;
    padding: 12px;
    display: flex;
    flex-direction: column;

    .sidebar-title {
      font-size: 13px;
      font-weight: 600;
      color: #1f2328;
      margin-bottom: 6px;
    }

    .file-list-scroll {
      flex: 1;
      overflow: hidden;

      .record-file-card {
        padding: 8px 10px;
        background: #f6f8fa;
        border-radius: 6px;
        margin-bottom: 6px;
        cursor: pointer;
        transition: all 0.2s ease;
        border-left: 3px solid transparent;

        &:hover {
          background: #edf2f7;
        }

        &.is-active {
          background: var(--el-color-primary-light-9);
          border-left-color: var(--el-color-primary);
        }

        .file-time-span {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 12px;
          font-weight: 500;
          color: #24292f;
        }

        .file-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: 4px;
          font-size: 11px;
          color: #8c959f;
        }
      }
    }
  }

  .playback-main {
    flex: 1;
    background: #fff;
    border-radius: 6px;
    padding: 12px;
    display: flex;
    flex-direction: column;

    .player-wrapper {
      position: relative;
      flex: 1;
      background: #000;
      border-radius: 6px;
      overflow: hidden;
      display: flex;
      align-items: center;
      justify-content: center;

      .playback-video {
        width: 100%;
        height: 100%;
        object-fit: contain;
      }

      .player-placeholder {
        position: absolute;
        inset: 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #8c959f;
        gap: 12px;
      }
    }

    .timeline-bar-wrapper {
      margin-top: 14px;
      background: #1f2328;
      border-radius: 6px;
      padding: 10px 16px;
      color: #8c959f;

      .timeline-scale {
        display: flex;
        justify-content: space-between;
        font-size: 10px;
        margin-bottom: 4px;
        user-select: none;
      }

      .timeline-track {
        position: relative;
        height: 28px;
        background: #2d333b;
        border-radius: 4px;
        cursor: pointer;
        overflow: hidden;

        .timeline-segment {
          position: absolute;
          top: 0;
          bottom: 0;
          background: #2ea043;
          opacity: 0.85;
          border-radius: 2px;
        }

        .timeline-cursor {
          position: absolute;
          top: 0;
          bottom: 0;
          width: 2px;
          background: #f85149;
          z-index: 5;

          .cursor-tag {
            position: absolute;
            top: -18px;
            left: -22px;
            background: #f85149;
            color: #fff;
            font-size: 9px;
            padding: 1px 4px;
            border-radius: 2px;
            white-space: nowrap;
          }
        }
      }
    }

    .playback-controls {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 10px;
      padding-top: 8px;
      border-top: 1px solid #f0f2f5;

      .controls-right {
        display: flex;
        align-items: center;
        gap: 8px;

        .speed-label {
          font-size: 12px;
          color: #57606a;
        }
      }
    }
  }
}
</style>
