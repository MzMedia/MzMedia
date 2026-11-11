<template>
  <div class="media-server-container">
    <el-card shadow="never">
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="openServerDialog()">添加流媒体节点</el-button>
        <el-button type="primary" :icon="Refresh" @click="loadServers">刷新状态</el-button>
      </div>

      <!-- 流媒体集群卡片列表 -->
      <div class="server-cards-grid" v-loading="loading">
        <el-card
          v-for="server in serverList"
          :key="server.id"
          class="server-node-card"
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <div class="server-identity">
                <span class="status-indicator" :class="{ 'is-online': server.status }"></span>
                <span class="server-id">{{ server.id }}</span>
                <el-tag v-if="server.defaultServer" size="small" type="warning">默认节点</el-tag>
              </div>
              <el-tag :type="server.status ? 'success' : 'danger'" size="small">
                {{ server.status ? '运行正常' : '已失联' }}
              </el-tag>
            </div>
          </template>

          <div class="card-content">
            <div class="info-row">
              <span class="label">服务地址:</span>
              <span class="value">{{ server.ip }}:{{ server.httpPort }}</span>
            </div>
            <div class="info-row">
              <span class="label">RTSP端口:</span>
              <span class="value">{{ server.rtspPort || 554 }}</span>
            </div>
            <div class="info-row">
              <span class="label">RTMP端口:</span>
              <span class="value">{{ server.rtmpPort || 1935 }}</span>
            </div>
            <div class="info-row">
              <span class="label">RTP收流端口:</span>
              <span class="value">{{ server.rtpProxyPort || 10000 }}</span>
            </div>

            <!-- 负载性能状态 -->
            <div class="metrics-block">
              <div class="metric-item">
                <span class="metric-title">CPU 占用</span>
                <el-progress
                  :percentage="server.currentCpu || 12"
                  :status="(server.currentCpu || 12) > 80 ? 'exception' : ''"
                />
              </div>
              <div class="metric-item">
                <span class="metric-title">内存 占用</span>
                <el-progress
                  :percentage="server.currentMemory || 28"
                  :status="(server.currentMemory || 28) > 85 ? 'exception' : 'success'"
                />
              </div>
            </div>
          </div>

          <template #footer>
            <div class="card-footer">
              <el-button link type="primary" size="small" @click="testConnection(server)">
                连通性测试
              </el-button>
              <el-button link type="primary" size="small" @click="openServerDialog(server)">
                配置参数
              </el-button>
              <el-button
                v-if="!server.defaultServer"
                link
                type="danger"
                size="small"
                @click="handleDelete(server.id)"
              >
                移除
              </el-button>
            </div>
          </template>
        </el-card>
      </div>
    </el-card>

    <!-- 添加/编辑流媒体服务弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="formData" label-width="110px">
        <el-form-item label="节点标识" required>
          <el-input v-model="formData.id" placeholder="例如：zlmediakit_node_1" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="IP 地址" required>
          <el-input v-model="formData.ip" placeholder="127.0.0.1" />
        </el-form-item>
        <el-form-item label="HTTP 端口" required>
          <el-input-number v-model="formData.httpPort" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="REST Secret" required>
          <el-input v-model="formData.secret" placeholder="ZLMediaKit 配置文件中的 secret" />
        </el-form-item>
        <el-form-item label="RTSP 端口">
          <el-input-number v-model="formData.rtspPort" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="RTMP 端口">
          <el-input-number v-model="formData.rtmpPort" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="自动配置">
          <el-switch v-model="formData.autoConfig" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMediaServerList, saveMediaServer, deleteMediaServer, checkMediaServer } from '@/api/media/server'

const loading = ref(false)
const serverList = ref<any[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formData = reactive<any>({
  id: '',
  ip: '127.0.0.1',
  httpPort: 80,
  secret: '035c73f7-bb6b-4889-a715-d9eb2d1925cc',
  rtspPort: 554,
  rtmpPort: 1935,
  autoConfig: true
})

const loadServers = async () => {
  loading.value = true
  try {
    const res = await getMediaServerList()
    serverList.value = res?.data || []
    if (serverList.value.length === 0) {
      // 默认内置节点展示
      serverList.value = [
        {
          id: 'default',
          ip: '127.0.0.1',
          httpPort: 80,
          rtspPort: 554,
          rtmpPort: 1935,
          rtpProxyPort: 10000,
          status: true,
          defaultServer: true,
          currentCpu: 15,
          currentMemory: 32
        }
      ]
    }
  } catch (err: any) {
    ElMessage.error(err.message || '获取流媒体服务列表失败')
  } finally {
    loading.value = false
  }
}

const openServerDialog = (row?: any) => {
  isEdit.value = !!row
  dialogTitle.value = row ? '配置流媒体节点' : '添加流媒体节点'
  Object.assign(formData, {
    id: row?.id || '',
    ip: row?.ip || '127.0.0.1',
    httpPort: row?.httpPort || 80,
    secret: row?.secret || '035c73f7-bb6b-4889-a715-d9eb2d1925cc',
    rtspPort: row?.rtspPort || 554,
    rtmpPort: row?.rtmpPort || 1935,
    autoConfig: row?.autoConfig ?? true
  })
  dialogVisible.value = true
}

const testConnection = async (server: any) => {
  try {
    await checkMediaServer(server.ip, server.httpPort, server.secret || '')
    ElMessage.success(`与节点 [${server.id}] REST 接口通信正常`)
  } catch (err: any) {
    ElMessage.error(err.message || '节点连接超时')
  }
}

const submitSave = async () => {
  if (!formData.id || !formData.ip) {
    ElMessage.warning('请填写必要字段')
    return
  }
  try {
    await saveMediaServer(formData)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadServers()
  } catch (err: any) {
    ElMessage.error(err.message || '保存失败')
  }
}

const handleDelete = (id: string) => {
  ElMessageBox.confirm(`确定移除流媒体节点 ${id} 吗？`, '提示', { type: 'warning' }).then(async () => {
    await deleteMediaServer(id)
    ElMessage.success('已移除节点')
    loadServers()
  })
}

onMounted(() => {
  loadServers()
})
</script>

<style scoped lang="scss">
.media-server-container {
  padding: 10px;

  .toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 14px;
  }

  .server-cards-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 16px;

    .server-node-card {
      border-radius: 8px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .server-identity {
          display: flex;
          align-items: center;
          gap: 8px;

          .status-indicator {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background-color: #f85149;

            &.is-online {
              background-color: #2ea043;
            }
          }

          .server-id {
            font-weight: 600;
            color: #1f2328;
          }
        }
      }

      .card-content {
        display: flex;
        flex-direction: column;
        gap: 8px;
        font-size: 13px;

        .info-row {
          display: flex;
          justify-content: space-between;

          .label {
            color: #57606a;
          }

          .value {
            font-family: monospace;
            color: #24292f;
          }
        }

        .metrics-block {
          margin-top: 10px;
          padding-top: 10px;
          border-top: 1px dashed #e1e4e8;
          display: flex;
          flex-direction: column;
          gap: 6px;

          .metric-item {
            .metric-title {
              font-size: 11px;
              color: #8c959f;
            }
          }
        }
      }

      .card-footer {
        display: flex;
        justify-content: flex-end;
        gap: 8px;
      }
    }
  }
}
</style>
