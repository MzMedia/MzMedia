<template>
  <div class="media-map-container">
    <!-- 左侧点位树 -->
    <div class="map-sidebar">
      <div class="sidebar-header">
        <span class="title">地图监控点位</span>
        <el-button link type="primary" :icon="Refresh" @click="loadChannels" />
      </div>
      <el-input
        v-model="searchKey"
        placeholder="搜索地图点位"
        clearable
        size="small"
        :prefix-icon="Search"
        style="margin-bottom: 10px"
      />
      <div class="channel-list">
        <el-scrollbar>
          <div
            v-for="ch in filteredChannels"
            :key="ch.id"
            class="channel-map-item"
            :class="{ 'is-selected': currentChannel?.id === ch.id }"
            @click="focusChannelOnMap(ch)"
          >
            <el-icon class="icon" :class="{ 'is-online': ch.status }"><Location /></el-icon>
            <div class="channel-text">
              <span class="name">{{ ch.name || ch.channelId }}</span>
              <span class="coords">经度: {{ ch.longitude || 116.397 }}, 纬度: {{ ch.latitude || 39.908 }}</span>
            </div>
          </div>
        </el-scrollbar>
      </div>
    </div>

    <!-- 右侧电子地图主视区 -->
    <div class="map-view">
      <!-- 模拟高精度电子地图网格背景与点位标注 -->
      <div class="map-canvas">
        <div class="map-grid-layer"></div>
        <div class="map-roads-layer">
          <div class="road horizontal road-1">科技大道</div>
          <div class="road horizontal road-2">园区南路</div>
          <div class="road vertical road-3">迎宾大道</div>
        </div>

        <!-- 摄像机标注点位 -->
        <div
          v-for="(ch, idx) in positionedChannels"
          :key="ch.id"
          class="camera-marker"
          :class="{ 'is-active': currentChannel?.id === ch.id, 'is-online': ch.status }"
          :style="{ left: `${ch.markerX}%`, top: `${ch.markerY}%` }"
          @click="openChannelPopup(ch)"
        >
          <div class="marker-pulse"></div>
          <div class="marker-icon">
            <el-icon :size="16"><VideoCamera /></el-icon>
          </div>
          <div class="marker-label">{{ ch.name || `通道 ${idx + 1}` }}</div>
        </div>
      </div>

      <!-- 地图工具浮窗 (缩放、图层) -->
      <div class="map-toolbar">
        <el-button-group>
          <el-button :icon="ZoomIn" size="small" @click="zoomLevel += 1" />
          <el-button :icon="ZoomOut" size="small" @click="zoomLevel = Math.max(1, zoomLevel - 1)" />
        </el-button-group>
      </div>
    </div>

    <!-- 点位实时监控弹窗 -->
    <el-dialog
      v-model="previewDialogVisible"
      :title="`实时监控 - ${currentChannel?.name || ''}`"
      width="780px"
      destroy-on-close
    >
      <div style="height: 440px">
        <MediaLivePlayer
          :channel-title="currentChannel?.name"
          :device-id="currentChannel?.deviceId"
          :channel-id="currentChannel?.channelId"
          :stream-sources="streamSources"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Search, Refresh, Location, VideoCamera, ZoomIn, ZoomOut } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import MediaLivePlayer from '@/components/MediaLivePlayer/index.vue'
import { getMapChannelList, playChannel, MediaChannelVO } from '@/api/media/channel'

const searchKey = ref('')
const channelList = ref<MediaChannelVO[]>([])
const currentChannel = ref<MediaChannelVO | null>(null)
const previewDialogVisible = ref(false)
const streamSources = ref<any>(null)
const zoomLevel = ref(12)

const filteredChannels = computed(() => {
  if (!searchKey.value) return channelList.value
  const kw = searchKey.value.toLowerCase()
  return channelList.value.filter(
    (c) => c.name?.toLowerCase().includes(kw) || c.channelId?.toLowerCase().includes(kw)
  )
})

// 计算点位在地图画布上的投影坐标
const positionedChannels = computed(() => {
  return channelList.value.map((c, idx) => {
    // 伪散布坐标
    const markerX = 18 + ((idx * 17) % 68)
    const markerY = 20 + ((idx * 23) % 60)
    return {
      ...c,
      markerX,
      markerY
    }
  })
})

const loadChannels = async () => {
  try {
    const res = await getMapChannelList()
    channelList.value = res?.data || []
    if (channelList.value.length === 0) {
      // 模拟若干预置点位
      channelList.value = [
        { id: 1, channelId: '34020000001320000001', deviceId: '34020000001110000001', name: '园区主入口东枪机', status: true, longitude: 116.3974, latitude: 39.9092 },
        { id: 2, channelId: '34020000001320000002', deviceId: '34020000001110000001', name: '研发楼大堂全景球机', status: true, longitude: 116.3985, latitude: 39.9078 },
        { id: 3, channelId: '34020000001320000003', deviceId: '34020000001110000002', name: '南区地下车库坡道', status: true, longitude: 116.3962, latitude: 39.9065 },
        { id: 4, channelId: '34020000001320000004', deviceId: '34020000001110000002', name: '周界防越电子围栏01', status: false, longitude: 116.3991, latitude: 39.9055 }
      ]
    }
  } catch (err: any) {
    ElMessage.error(err.message || '加载地图点位失败')
  }
}

const focusChannelOnMap = (ch: MediaChannelVO) => {
  currentChannel.value = ch
}

const openChannelPopup = async (ch: MediaChannelVO) => {
  currentChannel.value = ch
  previewDialogVisible.value = true
  try {
    const res = await playChannel(ch.id)
    streamSources.value = res?.data
  } catch (e: any) {
    ElMessage.error(e.message || '点播失败')
  }
}

onMounted(() => {
  loadChannels()
})
</script>

<style scoped lang="scss">
.media-map-container {
  display: flex;
  height: calc(100vh - 120px);
  gap: 10px;
  padding: 10px;
  background-color: #f0f2f5;

  .map-sidebar {
    width: 280px;
    background: #fff;
    border-radius: 6px;
    padding: 12px;
    display: flex;
    flex-direction: column;

    .sidebar-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .title {
        font-size: 13px;
        font-weight: 600;
        color: #1f2328;
      }
    }

    .channel-list {
      flex: 1;
      overflow: hidden;

      .channel-map-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px;
        border-radius: 4px;
        cursor: pointer;
        transition: background 0.2s ease;
        margin-bottom: 4px;

        &:hover {
          background: #f6f8fa;
        }

        &.is-selected {
          background: var(--el-color-primary-light-9);
          border-left: 3px solid var(--el-color-primary);
        }

        .icon {
          color: #8c959f;
          &.is-online {
            color: #2ea043;
          }
        }

        .channel-text {
          display: flex;
          flex-direction: column;
          overflow: hidden;

          .name {
            font-size: 12px;
            color: #24292f;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .coords {
            font-size: 10px;
            color: #8c959f;
          }
        }
      }
    }
  }

  .map-view {
    flex: 1;
    position: relative;
    background: #111827;
    border-radius: 6px;
    overflow: hidden;

    .map-canvas {
      position: absolute;
      inset: 0;

      .map-grid-layer {
        position: absolute;
        inset: 0;
        background-size: 40px 40px;
        background-image: linear-gradient(to right, rgba(255, 255, 255, 0.05) 1px, transparent 1px),
          linear-gradient(to bottom, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
      }

      .map-roads-layer {
        position: absolute;
        inset: 0;

        .road {
          position: absolute;
          background: rgba(59, 130, 246, 0.15);
          color: rgba(255, 255, 255, 0.4);
          font-size: 11px;
          display: flex;
          align-items: center;
          justify-content: center;

          &.horizontal {
            height: 18px;
            left: 0;
            right: 0;
          }

          &.vertical {
            width: 20px;
            top: 0;
            bottom: 0;
            writing-mode: vertical-lr;
          }

          &.road-1 {
            top: 30%;
          }
          &.road-2 {
            top: 75%;
          }
          &.road-3 {
            left: 45%;
          }
        }
      }

      .camera-marker {
        position: absolute;
        transform: translate(-50%, -50%);
        cursor: pointer;
        display: flex;
        flex-direction: column;
        align-items: center;
        z-index: 10;

        .marker-icon {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          background: #374151;
          color: #9ca3af;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 2px solid #4b5563;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
          transition: transform 0.2s ease;
        }

        .marker-label {
          margin-top: 4px;
          background: rgba(17, 24, 39, 0.85);
          color: #f3f4f6;
          font-size: 11px;
          padding: 2px 6px;
          border-radius: 4px;
          white-space: nowrap;
          border: 1px solid rgba(255, 255, 255, 0.1);
        }

        &.is-online .marker-icon {
          background: #10b981;
          color: #fff;
          border-color: #34d399;
        }

        &.is-active .marker-icon {
          transform: scale(1.25);
          border-color: #f59e0b;
        }
      }
    }

    .map-toolbar {
      position: absolute;
      top: 14px;
      right: 14px;
      z-index: 20;
    }
  }
}
</style>
