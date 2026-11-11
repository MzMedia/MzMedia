<template>
  <div class="media-live-container">
    <!-- 左侧设备与通道树 -->
    <div class="channel-tree-sidebar">
      <div class="sidebar-header">
        <span class="sidebar-title">监控通道目录</span>
        <el-button link type="primary" :icon="Refresh" @click="loadChannelTree" title="刷新列表" />
      </div>
      <div class="sidebar-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索通道名称/设备国标号"
          clearable
          size="small"
          :prefix-icon="Search"
        />
      </div>
      <div class="tree-content" v-loading="treeLoading">
        <el-scrollbar>
          <div
            v-for="channel in filteredChannels"
            :key="channel.id"
            class="channel-item"
            :class="{
              'is-active': activeChannel?.id === channel.id,
              'is-online': isChannelOnline(channel)
            }"
            @click="selectChannel(channel)"
            @dblclick="playChannelInActiveTile(channel)"
          >
            <div class="channel-info">
              <el-icon class="channel-icon" :size="16">
                <VideoCamera />
              </el-icon>
              <span class="channel-name" :title="channel.name">
                {{ channel.name || `通道 ${channel.channelId}` }}
              </span>
            </div>
            <div class="channel-meta">
              <el-tag size="small" :type="isChannelOnline(channel) ? 'success' : 'info'">
                {{ getProtocolName(channel.dataType) }}
              </el-tag>
            </div>
          </div>

          <el-empty
            v-if="filteredChannels.length === 0 && !treeLoading"
            description="暂无通道数据"
            :image-size="60"
          />
        </el-scrollbar>
      </div>
    </div>

    <!-- 右侧多分屏监控主区域 -->
    <div class="live-main-content">
      <!-- 顶部控制条 (分屏选择、批量操作) -->
      <div class="live-action-bar">
        <div class="action-bar-left">
          <span class="active-channel-info" v-if="activeChannel">
            当前选中: <strong>{{ activeChannel.name }}</strong> ({{ activeChannel.deviceId }})
          </span>
          <span class="active-channel-info" v-else>
            提示: 点击分屏格子后，双击左侧通道即可播放
          </span>
        </div>

        <div class="action-bar-right">
          <!-- 分屏模式切换 -->
          <el-radio-group v-model="splitCount" size="small" @change="handleSplitChange">
            <el-radio-button :value="1">单屏</el-radio-button>
            <el-radio-button :value="4">四分屏</el-radio-button>
            <el-radio-button :value="9">九分屏</el-radio-button>
            <el-radio-button :value="16">十六分屏</el-radio-button>
          </el-radio-group>

          <el-button
            size="small"
            type="danger"
            plain
            :icon="Close"
            @click="stopAllPlayers"
            title="关闭所有分屏视频"
          >
            全部停止
          </el-button>
        </div>
      </div>

      <!-- 多分屏网格容器 -->
      <div class="video-grid-container" :class="`grid-split-${splitCount}`">
        <div
          v-for="(tile, index) in tiles.slice(0, splitCount)"
          :key="index"
          class="video-grid-tile"
          :class="{ 'is-selected': activeTileIndex === index }"
          @click="activeTileIndex = index"
        >
          <MediaLivePlayer
            :channel-title="tile.channel?.name"
            :device-id="tile.channel?.deviceId"
            :channel-id="tile.channel?.channelId"
            :stream-sources="tile.streamSources"
            :empty-text="`分屏 ${index + 1} (点击选择)`"
            @stop="onTileStop(index)"
          />
          <div class="tile-badge">#{{ index + 1 }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Search, Refresh, VideoCamera, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import MediaLivePlayer from '@/components/MediaLivePlayer/index.vue'
import { getChannelPage, playChannel, stopPlayChannel, MediaChannelVO } from '@/api/media/channel'

interface GridTile {
  channel: MediaChannelVO | null
  streamSources: any | null
}

const treeLoading = ref(false)
const searchKeyword = ref('')
const channelList = ref<MediaChannelVO[]>([])
const activeChannel = ref<MediaChannelVO | null>(null)
const splitCount = ref<number>(4)
const activeTileIndex = ref<number>(0)

// 初始化 16 个网格槽位
const tiles = ref<GridTile[]>(
  Array.from({ length: 16 }, () => ({
    channel: null,
    streamSources: null
  }))
)

const filteredChannels = computed(() => {
  if (!searchKeyword.value) return channelList.value
  const kw = searchKeyword.value.toLowerCase()
  return channelList.value.filter(
    (c) =>
      c.name?.toLowerCase().includes(kw) ||
      c.channelId?.toLowerCase().includes(kw) ||
      c.deviceId?.toLowerCase().includes(kw)
  )
})

const isChannelOnline = (channel: MediaChannelVO) => {
  return channel.status === true || channel.status === 1
}

const getProtocolName = (dataType?: number) => {
  switch (dataType) {
    case 0:
      return 'GB28181'
    case 1:
      return '拉流代理'
    case 2:
      return '推流'
    case 4:
      return 'ONVIF'
    case 5:
      return '海康'
    case 6:
      return '大华'
    case 7:
      return '萤石云'
    case 200:
      return 'JT1078'
    default:
      return '视频流'
  }
}

// 加载通道列表
const loadChannelTree = async () => {
  treeLoading.value = true
  try {
    const res = await getChannelPage({ page: 1, count: 100 })
    if (res && res.data) {
      channelList.value = res.data.list || res.data || []
    }
  } catch (e: any) {
    ElMessage.error(e.message || '获取通道列表失败')
  } finally {
    treeLoading.value = false
  }
}

const selectChannel = (channel: MediaChannelVO) => {
  activeChannel.value = channel
}

// 在当前激活的分屏格子中播放通道
const playChannelInActiveTile = async (channel: MediaChannelVO) => {
  activeChannel.value = channel
  const targetIndex = activeTileIndex.value

  try {
    ElMessage.info(`正在点播: ${channel.name}...`)
    const res = await playChannel(channel.id)
    if (res && res.data) {
      tiles.value[targetIndex] = {
        channel: channel,
        streamSources: res.data
      }
      ElMessage.success(`点播成功 [分屏 #${targetIndex + 1}]`)
      // 自动推进到下一个分屏槽位
      activeTileIndex.value = (targetIndex + 1) % splitCount.value
    } else {
      ElMessage.warning('未能获取流地址')
    }
  } catch (err: any) {
    ElMessage.error(err.message || '点播失败')
  }
}

const onTileStop = (index: number) => {
  const tile = tiles.value[index]
  if (tile.channel) {
    stopPlayChannel(tile.channel.id).catch(() => {})
    tile.channel = null
    tile.streamSources = null
  }
}

const stopAllPlayers = () => {
  tiles.value.forEach((_tile, index) => {
    onTileStop(index)
  })
  ElMessage.success('已停止所有分屏点播')
}

const handleSplitChange = (val: number) => {
  if (activeTileIndex.value >= val) {
    activeTileIndex.value = 0
  }
}

onMounted(() => {
  loadChannelTree()
})
</script>

<style scoped lang="scss">
.media-live-container {
  display: flex;
  height: calc(100vh - 120px);
  background-color: #f0f2f5;
  border-radius: 8px;
  overflow: hidden;
  gap: 8px;
  padding: 8px;

  .channel-tree-sidebar {
    width: 280px;
    background: #fff;
    border-radius: 6px;
    display: flex;
    flex-direction: column;
    padding: 12px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

    .sidebar-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .sidebar-title {
        font-size: 14px;
        font-weight: 600;
        color: #1f2328;
      }
    }

    .sidebar-search {
      margin-bottom: 10px;
    }

    .tree-content {
      flex: 1;
      overflow: hidden;

      .channel-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 8px 10px;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.2s ease;
        margin-bottom: 4px;

        &:hover {
          background-color: #f6f8fa;
        }

        &.is-active {
          background-color: var(--el-color-primary-light-9);
          border-left: 3px solid var(--el-color-primary);
        }

        .channel-info {
          display: flex;
          align-items: center;
          gap: 8px;
          overflow: hidden;

          .channel-icon {
            color: #8c959f;
          }

          .channel-name {
            font-size: 13px;
            color: #24292f;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            max-width: 140px;
          }
        }

        &.is-online .channel-icon {
          color: #2da44e;
        }
      }
    }
  }

  .live-main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: #fff;
    border-radius: 6px;
    overflow: hidden;
    padding: 10px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

    .live-action-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-bottom: 10px;
      border-bottom: 1px solid #f0f2f5;

      .active-channel-info {
        font-size: 13px;
        color: #57606a;
      }

      .action-bar-right {
        display: flex;
        align-items: center;
        gap: 12px;
      }
    }

    .video-grid-container {
      flex: 1;
      display: grid;
      gap: 6px;
      margin-top: 10px;
      overflow: hidden;

      &.grid-split-1 {
        grid-template-columns: 1fr;
        grid-template-rows: 1fr;
      }

      &.grid-split-4 {
        grid-template-columns: repeat(2, 1fr);
        grid-template-rows: repeat(2, 1fr);
      }

      &.grid-split-9 {
        grid-template-columns: repeat(3, 1fr);
        grid-template-rows: repeat(3, 1fr);
      }

      &.grid-split-16 {
        grid-template-columns: repeat(4, 1fr);
        grid-template-rows: repeat(4, 1fr);
      }

      .video-grid-tile {
        position: relative;
        background: #000;
        border-radius: 6px;
        overflow: hidden;
        border: 2px solid transparent;
        transition: border-color 0.2s ease;

        &.is-selected {
          border-color: var(--el-color-primary);
        }

        .tile-badge {
          position: absolute;
          top: 6px;
          left: 6px;
          background: rgba(0, 0, 0, 0.5);
          color: #fff;
          font-size: 10px;
          padding: 1px 4px;
          border-radius: 3px;
          pointer-events: none;
          z-index: 10;
        }
      }
    }
  }
}
</style>
