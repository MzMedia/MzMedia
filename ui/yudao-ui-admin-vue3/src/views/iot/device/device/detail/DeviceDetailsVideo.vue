<template>
  <div class="device-details-video" v-loading="loading">
    <el-row :gutter="16">
      <!-- 视频播放器主区域 -->
      <el-col :span="16">
        <el-card shadow="never" class="video-card">
          <template #header>
            <div class="card-header">
              <span class="title">实时视频监控</span>
              <div class="header-actions">
                <el-tag size="small" type="success">{{ parsedConfig.protocol || 'GB28181' }}</el-tag>
                <el-button link type="primary" :icon="Refresh" @click="loadStream">重新拉流</el-button>
              </div>
            </div>
          </template>

          <div class="player-box">
            <MediaLivePlayer
              :channel-title="device.nickname || device.deviceName"
              :device-id="parsedConfig.deviceId || device.deviceName"
              :channel-id="parsedConfig.channelGbId || '1'"
              :stream-sources="streamSources"
              :has-ptz="parsedConfig.hasPtz ?? true"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 视频参数与联动配置信息 -->
      <el-col :span="8">
        <el-card shadow="never" class="config-card">
          <template #header>
            <div class="card-header">
              <span class="title">视频流配置参数</span>
            </div>
          </template>

          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="接入协议">
              <el-tag size="small">{{ parsedConfig.protocol || 'GB28181' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="关联通道ID">
              {{ parsedConfig.channelId || parsedConfig.channelGbId || '未绑定' }}
            </el-descriptions-item>
            <el-descriptions-item label="设备国标ID">
              {{ parsedConfig.deviceId || device.deviceName }}
            </el-descriptions-item>
            <el-descriptions-item label="云台控制 (PTZ)">
              <el-tag :type="parsedConfig.hasPtz !== false ? 'success' : 'info'" size="small">
                {{ parsedConfig.hasPtz !== false ? '支持' : '不支持' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="推拉流地址" v-if="parsedConfig.streamUrl">
              <span class="break-all">{{ parsedConfig.streamUrl }}</span>
            </el-descriptions-item>
          </el-descriptions>

          <div class="tips-box">
            <el-alert
              title="IoT 规则联动提示"
              type="info"
              description="本设备可参与 IoT 场景规则联动：当发生传感器超限报警时，可触发自动联动调用摄像机预置位或抓拍照片存证。"
              :closable="false"
              show-icon
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import MediaLivePlayer from '@/components/MediaLivePlayer/index.vue'
import { playChannel } from '@/api/media/channel'

const props = defineProps({
  device: {
    type: Object,
    required: true
  },
  product: {
    type: Object,
    required: true
  }
})

const loading = ref(false)
const streamSources = ref<any>(null)

const parsedConfig = computed(() => {
  if (!props.device.videoConfig) {
    return {
      protocol: 'GB28181',
      hasPtz: true,
      deviceId: props.device.deviceName
    }
  }
  try {
    return typeof props.device.videoConfig === 'string'
      ? JSON.parse(props.device.videoConfig)
      : props.device.videoConfig
  } catch (e) {
    return { protocol: 'GB28181', hasPtz: true }
  }
})

const loadStream = async () => {
  const config = parsedConfig.value
  if (!config.channelId && !config.streamUrl) {
    // 构造默认预览数据
    streamSources.value = null
    return
  }

  loading.value = true
  try {
    if (config.channelId) {
      const res = await playChannel(config.channelId)
      streamSources.value = res?.data
    } else if (config.streamUrl) {
      streamSources.value = {
        flv: config.streamUrl,
        rtc: config.streamUrl
      }
    }
  } catch (err: any) {
    ElMessage.warning('未能拉取到流地址，请检查流媒体服务是否在线')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStream()
})
</script>

<style scoped lang="scss">
.device-details-video {
  padding: 12px 0;

  .video-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title {
        font-weight: 600;
        font-size: 14px;
      }

      .header-actions {
        display: flex;
        align-items: center;
        gap: 10px;
      }
    }

    .player-box {
      height: 480px;
      background: #000;
      border-radius: 6px;
      overflow: hidden;
    }
  }

  .config-card {
    .card-header {
      .title {
        font-weight: 600;
        font-size: 14px;
      }
    }

    .break-all {
      word-break: break-all;
      font-size: 11px;
    }

    .tips-box {
      margin-top: 16px;
    }
  }
}
</style>
