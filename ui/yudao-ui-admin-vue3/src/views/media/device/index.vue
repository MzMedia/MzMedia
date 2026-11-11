<template>
  <div class="media-device-management">
    <el-card shadow="never">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 1. GB28181 国标设备 -->
        <el-tab-pane label="GB28181 国标设备" name="gb28181">
          <div class="table-toolbar">
            <el-input
              v-model="gbSearch"
              placeholder="搜索设备国标编号或名称"
              clearable
              style="width: 260px"
              :prefix-icon="Search"
              @change="loadGbDevices"
            />
            <el-button type="primary" :icon="Refresh" @click="loadGbDevices">刷新</el-button>
          </div>

          <el-table :data="gbDeviceList" v-loading="loading" style="width: 100%" border>
            <el-table-column prop="deviceId" label="设备国标编号" min-width="180" />
            <el-table-column prop="name" label="设备名称" min-width="150" />
            <el-table-column prop="manufacturer" label="生产厂商" width="130" />
            <el-table-column prop="model" label="设备型号" width="130" />
            <el-table-column prop="hostAddress" label="信令地址" width="160" />
            <el-table-column prop="channelCount" label="通道数" width="90" align="center" />
            <el-table-column prop="streamMode" label="传输流模式" width="110" align="center">
              <template #default="{ row }">
                <el-tag size="small">{{ row.streamMode || 'UDP' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="onLine" label="在线状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.onLine ? 'success' : 'danger'" size="small">
                  {{ row.onLine ? '在线' : '离线' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="keepaliveTime" label="最后心跳" width="170" />
            <el-table-column label="操作" width="200" fixed="right" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="openChannelDrawer(row.deviceId, 'gb28181')">
                  通道列表
                </el-button>
                <el-button link type="warning" @click="syncGbChannels(row.deviceId)">
                  同步通道
                </el-button>
                <el-button link type="danger" @click="handleDeleteGb(row.deviceId)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 2. ONVIF 设备 -->
        <el-tab-pane label="ONVIF 协议设备" name="onvif">
          <div class="table-toolbar">
            <div class="toolbar-left">
              <el-button type="success" :icon="Search" @click="openOnvifProbeDialog">
                局域网探测发现 (Probe)
              </el-button>
              <el-button type="primary" :icon="Plus" @click="openOnvifForm()">
                手动添加 ONVIF 设备
              </el-button>
            </div>
            <el-button type="primary" :icon="Refresh" @click="loadOnvifDevices">刷新</el-button>
          </div>

          <el-table :data="onvifDeviceList" v-loading="loading" style="width: 100%" border>
            <el-table-column prop="id" label="ID" width="70" align="center" />
            <el-table-column prop="name" label="设备名称" min-width="160" />
            <el-table-column prop="ip" label="IP 地址" width="140" />
            <el-table-column prop="port" label="端口" width="90" align="center" />
            <el-table-column prop="username" label="鉴权用户名" width="120" />
            <el-table-column prop="profiles" label="Profiles" min-width="140" show-overflow-tooltip />
            <el-table-column prop="streamUri" label="取流 RTSP 地址" min-width="220" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '正常' : '异常' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleSyncOnvif(row.id)">同步流</el-button>
                <el-button link type="primary" @click="openOnvifForm(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteOnvif(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 3. 海康威视 ISAPI -->
        <el-tab-pane label="海康威视 (ISAPI)" name="hikvision">
          <div class="table-toolbar">
            <el-button type="primary" :icon="Plus" @click="openHikForm()">
              添加海康设备
            </el-button>
            <el-button type="primary" :icon="Refresh" @click="loadHikDevices">刷新</el-button>
          </div>

          <el-table :data="hikDeviceList" v-loading="loading" style="width: 100%" border>
            <el-table-column prop="id" label="ID" width="70" align="center" />
            <el-table-column prop="name" label="设备名称" min-width="160" />
            <el-table-column prop="ip" label="IP 地址" width="140" />
            <el-table-column prop="port" label="HTTP端口" width="100" align="center" />
            <el-table-column prop="username" label="管理员账号" width="120" />
            <el-table-column prop="channels" label="模拟/数字通道数" width="130" align="center" />
            <el-table-column prop="deviceInfo" label="设备信息" min-width="160" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '在线' : '离线' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleSyncHik(row.id)">同步通道</el-button>
                <el-button link type="primary" @click="openHikForm(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteHik(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 4. 大华 (RPC / CGI) -->
        <el-tab-pane label="大华 (RPC/CGI)" name="dahua">
          <div class="table-toolbar">
            <el-button type="primary" :icon="Plus" @click="openDahuaForm()">
              添加大华设备
            </el-button>
            <el-button type="primary" :icon="Refresh" @click="loadDahuaDevices">刷新</el-button>
          </div>

          <el-table :data="dahuaDeviceList" v-loading="loading" style="width: 100%" border>
            <el-table-column prop="id" label="ID" width="70" align="center" />
            <el-table-column prop="name" label="设备名称" min-width="160" />
            <el-table-column prop="ip" label="IP 地址" width="140" />
            <el-table-column prop="port" label="端口" width="90" align="center" />
            <el-table-column prop="username" label="账号" width="120" />
            <el-table-column prop="channels" label="通道数" width="90" align="center" />
            <el-table-column prop="deviceInfo" label="设备型号信息" min-width="160" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '在线' : '离线' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleSyncDahua(row.id)">同步通道</el-button>
                <el-button link type="primary" @click="openDahuaForm(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteDahua(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 5. 萤石云 (Ezviz Open Cloud) -->
        <el-tab-pane label="萤石云 (Ezviz)" name="ezviz">
          <div class="table-toolbar">
            <el-button type="primary" :icon="Plus" @click="openEzvizForm()">
              添加萤石云设备
            </el-button>
            <el-button type="primary" :icon="Refresh" @click="loadEzvizDevices">刷新</el-button>
          </div>

          <el-table :data="ezvizDeviceList" v-loading="loading" style="width: 100%" border>
            <el-table-column prop="id" label="ID" width="70" align="center" />
            <el-table-column prop="name" label="设备名称" min-width="160" />
            <el-table-column prop="deviceSerial" label="设备序列号 (Serial)" width="160" />
            <el-table-column prop="channelNo" label="通道号" width="90" align="center" />
            <el-table-column prop="appKey" label="AppKey" width="140" show-overflow-tooltip />
            <el-table-column prop="liveAddress" label="直播地址 (HLS/FLV)" min-width="220" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '在线' : '离线' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleSyncEzviz(row.id)">获取直播流</el-button>
                <el-button link type="primary" @click="openEzvizForm(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteEzviz(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 6. 部标 JT/T 1078 (车载视频) -->
        <el-tab-pane label="部标 JT/T 1078 (车载视频)" name="jt1078">
          <div class="table-toolbar">
            <div class="toolbar-left">
              <el-button type="primary" :icon="Plus" @click="openJT1078Form()">
                添加 1078 车载设备
              </el-button>
              <el-tag type="info" size="large">网关监听端口: 1078 (RTP视频) / 7078 (信令)</el-tag>
            </div>
            <el-button type="primary" :icon="Refresh" @click="loadJT1078Devices">刷新</el-button>
          </div>

          <el-table :data="jt1078DeviceList" v-loading="loading" style="width: 100%" border>
            <el-table-column prop="id" label="ID" width="70" align="center" />
            <el-table-column prop="simNumber" label="SIM卡号 / 终端手机号" min-width="160" />
            <el-table-column prop="channel" label="逻辑通道号" width="110" align="center" />
            <el-table-column prop="plateNumber" label="绑定车牌号" width="130" />
            <el-table-column prop="streamId" label="转推流 ID" min-width="160" show-overflow-tooltip />
            <el-table-column prop="status" label="推流状态" width="110" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                  {{ row.status === 1 ? '推流中' : '未推流' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="openJT1078Form(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteJT1078(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 7. 拉流代理 (StreamProxy) -->
        <el-tab-pane label="拉流代理 (RTSP/RTMP/FLV)" name="proxy">
          <div class="table-toolbar">
            <el-button type="primary" :icon="Plus" @click="openProxyForm()">
              添加拉流代理
            </el-button>
            <el-button type="primary" :icon="Refresh" @click="loadStreamProxies">刷新</el-button>
          </div>

          <el-table :data="proxyList" v-loading="loading" style="width: 100%" border>
            <el-table-column prop="name" label="代理名称" min-width="150" />
            <el-table-column prop="app" label="应用名 (App)" width="110" />
            <el-table-column prop="stream" label="流ID (Stream)" width="150" />
            <el-table-column prop="url" label="源 RTSP/RTMP 地址" min-width="240" show-overflow-tooltip />
            <el-table-column prop="enable" label="启用状态" width="100" align="center">
              <template #default="{ row }">
                <el-switch
                  v-model="row.enable"
                  @change="(val: boolean) => toggleProxyStatus(row, val)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="status" label="拉流状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status ? 'success' : 'info'" size="small">
                  {{ row.status ? '拉流中' : '未激活' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="openProxyForm(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteProxy(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- ONVIF 探测对话框 -->
    <el-dialog v-model="probeDialogVisible" title="局域网 ONVIF 设备探测 (WS-Discovery)" width="700px">
      <div v-loading="probing" element-loading-text="正在向局域网广播 WS-Discovery 探测包...">
        <div class="probe-tips">
          系统已向局域网 239.255.255.250:3702 发送多播探测，发现以下兼容设备：
        </div>
        <el-table :data="probedDevices" border style="width: 100%; margin-top: 12px">
          <el-table-column prop="ip" label="设备 IP" width="140" />
          <el-table-column prop="port" label="端口" width="90" align="center" />
          <el-table-column prop="urn" label="ONVIF Endpoint / URN" min-width="260" show-overflow-tooltip />
          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="adoptProbedDevice(row)">导入</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="probeDialogVisible = false">关闭</el-button>
        <el-button type="primary" :icon="Refresh" @click="executeProbe">重新探测</el-button>
      </template>
    </el-dialog>

    <!-- 通用协议设备添加/编辑对话框 -->
    <el-dialog v-model="formDialogVisible" :title="formTitle" width="540px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="110px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="formData.name" placeholder="例如：大门东侧高清枪机" />
        </el-form-item>

        <template v-if="['onvif', 'hikvision', 'dahua'].includes(activeTab)">
          <el-form-item label="IP 地址" prop="ip">
            <el-input v-model="formData.ip" placeholder="192.168.1.100" />
          </el-form-item>
          <el-form-item label="端口号" prop="port">
            <el-input-number v-model="formData.port" :min="1" :max="65535" />
          </el-form-item>
          <el-form-item label="账号" prop="username">
            <el-input v-model="formData.username" placeholder="admin" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="formData.password" type="password" show-password />
          </el-form-item>
        </template>

        <template v-if="activeTab === 'ezviz'">
          <el-form-item label="AppKey" prop="appKey">
            <el-input v-model="formData.appKey" placeholder="萤石开放平台 AppKey" />
          </el-form-item>
          <el-form-item label="AppSecret" prop="appSecret">
            <el-input v-model="formData.appSecret" type="password" show-password />
          </el-form-item>
          <el-form-item label="设备序列号" prop="deviceSerial">
            <el-input v-model="formData.deviceSerial" placeholder="9位机身序列号" />
          </el-form-item>
          <el-form-item label="通道号" prop="channelNo">
            <el-input-number v-model="formData.channelNo" :min="1" :max="16" />
          </el-form-item>
          <el-form-item label="验证码" prop="validateCode">
            <el-input v-model="formData.validateCode" placeholder="机身标签 6位大写验证码" />
          </el-form-item>
        </template>

        <template v-if="activeTab === 'jt1078'">
          <el-form-item label="SIM卡号" prop="simNumber">
            <el-input v-model="formData.simNumber" placeholder="11位或12位车载终端手机号" />
          </el-form-item>
          <el-form-item label="通道号" prop="channel">
            <el-input-number v-model="formData.channel" :min="1" :max="8" />
          </el-form-item>
          <el-form-item label="车牌号" prop="plateNumber">
            <el-input v-model="formData.plateNumber" placeholder="例如：京A88888" />
          </el-form-item>
        </template>

        <template v-if="activeTab === 'proxy'">
          <el-form-item label="应用名 (App)" prop="app">
            <el-input v-model="formData.app" placeholder="live" />
          </el-form-item>
          <el-form-item label="流ID (Stream)" prop="stream">
            <el-input v-model="formData.stream" placeholder="test_stream" />
          </el-form-item>
          <el-form-item label="源流 RTSP/RTMP" prop="url">
            <el-input v-model="formData.url" placeholder="rtsp://admin:12345@192.168.1.50/h264/ch1/main/av_stream" />
          </el-form-item>
          <el-form-item label="启用音频">
            <el-switch v-model="formData.enableAudio" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定保存</el-button>
      </template>
    </el-dialog>

    <!-- 设备通道列表抽屉 -->
    <el-drawer v-model="channelDrawerVisible" :title="`设备通道列表 - ${currentDeviceId}`" size="680px">
      <el-table :data="deviceChannels" border style="width: 100%">
        <el-table-column prop="channelId" label="通道国标编号" min-width="180" />
        <el-table-column prop="name" label="通道名称" min-width="150" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'danger'" size="small">
              {{ row.status ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hasAudio" label="支持音频" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.hasAudio ? 'success' : 'info'" size="small">
              {{ row.hasAudio ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getGbDeviceList,
  syncGbDeviceChannels,
  deleteGbDevice,
  getGbDeviceChannels,
  getStreamProxyList,
  addStreamProxy,
  updateStreamProxy,
  deleteStreamProxy,
  startStreamProxy,
  stopStreamProxy
} from '@/api/media/device'
import {
  probeOnvifDevices,
  getOnvifDeviceList,
  saveOnvifDevice,
  deleteOnvifDevice,
  syncOnvifDevice,
  getHikDeviceList,
  saveHikDevice,
  deleteHikDevice,
  syncHikDevice,
  getDahuaDeviceList,
  saveDahuaDevice,
  deleteDahuaDevice,
  syncDahuaDevice,
  getEzvizDeviceList,
  saveEzvizDevice,
  deleteEzvizDevice,
  syncEzvizDevice,
  getJT1078DeviceList,
  saveJT1078Device,
  deleteJT1078Device
} from '@/api/media/protocol'

const activeTab = ref('gb28181')
const loading = ref(false)

// 数据源
const gbDeviceList = ref<any[]>([])
const gbSearch = ref('')
const onvifDeviceList = ref<any[]>([])
const hikDeviceList = ref<any[]>([])
const dahuaDeviceList = ref<any[]>([])
const ezvizDeviceList = ref<any[]>([])
const jt1078DeviceList = ref<any[]>([])
const proxyList = ref<any[]>([])

// 弹窗状态
const probeDialogVisible = ref(false)
const probing = ref(false)
const probedDevices = ref<any[]>([])

const formDialogVisible = ref(false)
const formTitle = ref('')
const formRef = ref()
const formData = reactive<any>({})
const formRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  ip: [{ required: true, message: '请输入 IP 地址', trigger: 'blur' }],
  port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
  appKey: [{ required: true, message: '请输入 AppKey', trigger: 'blur' }],
  appSecret: [{ required: true, message: '请输入 AppSecret', trigger: 'blur' }],
  deviceSerial: [{ required: true, message: '请输入序列号', trigger: 'blur' }],
  simNumber: [{ required: true, message: '请输入 SIM 卡号', trigger: 'blur' }],
  app: [{ required: true, message: '请输入应用名', trigger: 'blur' }],
  stream: [{ required: true, message: '请输入流 ID', trigger: 'blur' }],
  url: [{ required: true, message: '请输入拉流源地址', trigger: 'blur' }]
}

const channelDrawerVisible = ref(false)
const currentDeviceId = ref('')
const deviceChannels = ref<any[]>([])

const handleTabChange = (tab: any) => {
  if (tab === 'gb28181') loadGbDevices()
  else if (tab === 'onvif') loadOnvifDevices()
  else if (tab === 'hikvision') loadHikDevices()
  else if (tab === 'dahua') loadDahuaDevices()
  else if (tab === 'ezviz') loadEzvizDevices()
  else if (tab === 'jt1078') loadJT1078Devices()
  else if (tab === 'proxy') loadStreamProxies()
}

// 1. GB28181
const loadGbDevices = async () => {
  loading.value = true
  try {
    const res = await getGbDeviceList({ page: 1, count: 50, search: gbSearch.value })
    gbDeviceList.value = res?.data?.list || res?.data || []
  } finally {
    loading.value = false
  }
}

const syncGbChannels = async (deviceId: string) => {
  try {
    await syncGbDeviceChannels(deviceId)
    ElMessage.success('已触发通道同步指令')
  } catch (err: any) {
    ElMessage.error(err.message || '同步失败')
  }
}

const handleDeleteGb = (deviceId: string) => {
  ElMessageBox.confirm(`确定删除国标设备 ${deviceId} 吗？`, '提示', { type: 'warning' }).then(
    async () => {
      await deleteGbDevice(deviceId)
      ElMessage.success('删除成功')
      loadGbDevices()
    }
  )
}

const openChannelDrawer = async (deviceId: string, _type?: string) => {
  currentDeviceId.value = deviceId
  channelDrawerVisible.value = true
  try {
    const res = await getGbDeviceChannels(deviceId, { page: 1, count: 100 })
    deviceChannels.value = res?.data?.list || res?.data || []
  } catch (e: any) {
    deviceChannels.value = []
  }
}

// 2. ONVIF
const loadOnvifDevices = async () => {
  loading.value = true
  try {
    const res = await getOnvifDeviceList()
    onvifDeviceList.value = res?.data || []
  } finally {
    loading.value = false
  }
}

const openOnvifProbeDialog = () => {
  probeDialogVisible.value = true
  executeProbe()
}

const executeProbe = async () => {
  probing.value = true
  try {
    const res = await probeOnvifDevices()
    probedDevices.value = res?.data || []
  } catch (err: any) {
    ElMessage.error(err.message || '探测失败')
  } finally {
    probing.value = false
  }
}

const adoptProbedDevice = (row: any) => {
  probeDialogVisible.value = false
  openOnvifForm({
    name: `ONVIF_${row.ip}`,
    ip: row.ip,
    port: row.port || 80,
    username: 'admin',
    password: ''
  })
}

const openOnvifForm = (row?: any) => {
  formTitle.value = row?.id ? '编辑 ONVIF 设备' : '添加 ONVIF 设备'
  Object.assign(formData, {
    id: row?.id,
    name: row?.name || '',
    ip: row?.ip || '',
    port: row?.port || 80,
    username: row?.username || 'admin',
    password: row?.password || ''
  })
  formDialogVisible.value = true
}

const handleSyncOnvif = async (id: number) => {
  try {
    await syncOnvifDevice(id)
    ElMessage.success('ONVIF 协议探测与流地址获取成功')
    loadOnvifDevices()
  } catch (e: any) {
    ElMessage.error(e.message || '同步失败')
  }
}

const handleDeleteOnvif = (id: number) => {
  ElMessageBox.confirm('确定删除该 ONVIF 设备吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteOnvifDevice(id)
    ElMessage.success('删除成功')
    loadOnvifDevices()
  })
}

// 3. 海康
const loadHikDevices = async () => {
  loading.value = true
  try {
    const res = await getHikDeviceList()
    hikDeviceList.value = res?.data || []
  } finally {
    loading.value = false
  }
}

const openHikForm = (row?: any) => {
  formTitle.value = row?.id ? '编辑海康威视设备' : '添加海康威视设备'
  Object.assign(formData, {
    id: row?.id,
    name: row?.name || '',
    ip: row?.ip || '',
    port: row?.port || 80,
    username: row?.username || 'admin',
    password: row?.password || ''
  })
  formDialogVisible.value = true
}

const handleSyncHik = async (id: number) => {
  try {
    await syncHikDevice(id)
    ElMessage.success('海康 ISAPI 设备信息与通道同步成功')
    loadHikDevices()
  } catch (e: any) {
    ElMessage.error(e.message || '同步失败')
  }
}

const handleDeleteHik = (id: number) => {
  ElMessageBox.confirm('确定删除该海康设备吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteHikDevice(id)
    ElMessage.success('删除成功')
    loadHikDevices()
  })
}

// 4. 大华
const loadDahuaDevices = async () => {
  loading.value = true
  try {
    const res = await getDahuaDeviceList()
    dahuaDeviceList.value = res?.data || []
  } finally {
    loading.value = false
  }
}

const openDahuaForm = (row?: any) => {
  formTitle.value = row?.id ? '编辑大华设备' : '添加大华设备'
  Object.assign(formData, {
    id: row?.id,
    name: row?.name || '',
    ip: row?.ip || '',
    port: row?.port || 80,
    username: row?.username || 'admin',
    password: row?.password || ''
  })
  formDialogVisible.value = true
}

const handleSyncDahua = async (id: number) => {
  try {
    await syncDahuaDevice(id)
    ElMessage.success('大华设备 RPC 同步成功')
    loadDahuaDevices()
  } catch (e: any) {
    ElMessage.error(e.message || '同步失败')
  }
}

const handleDeleteDahua = (id: number) => {
  ElMessageBox.confirm('确定删除该大华设备吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteDahuaDevice(id)
    ElMessage.success('删除成功')
    loadDahuaDevices()
  })
}

// 5. 萤石云
const loadEzvizDevices = async () => {
  loading.value = true
  try {
    const res = await getEzvizDeviceList()
    ezvizDeviceList.value = res?.data || []
  } finally {
    loading.value = false
  }
}

const openEzvizForm = (row?: any) => {
  formTitle.value = row?.id ? '编辑萤石云设备' : '添加萤石云设备'
  Object.assign(formData, {
    id: row?.id,
    name: row?.name || '',
    appKey: row?.appKey || '',
    appSecret: row?.appSecret || '',
    deviceSerial: row?.deviceSerial || '',
    channelNo: row?.channelNo || 1,
    validateCode: row?.validateCode || ''
  })
  formDialogVisible.value = true
}

const handleSyncEzviz = async (id: number) => {
  try {
    await syncEzvizDevice(id)
    ElMessage.success('萤石云直播流获取成功')
    loadEzvizDevices()
  } catch (e: any) {
    ElMessage.error(e.message || '获取失败')
  }
}

const handleDeleteEzviz = (id: number) => {
  ElMessageBox.confirm('确定删除该萤石云设备吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteEzvizDevice(id)
    ElMessage.success('删除成功')
    loadEzvizDevices()
  })
}

// 6. JT1078
const loadJT1078Devices = async () => {
  loading.value = true
  try {
    const res = await getJT1078DeviceList()
    jt1078DeviceList.value = res?.data || []
  } finally {
    loading.value = false
  }
}

const openJT1078Form = (row?: any) => {
  formTitle.value = row?.id ? '编辑 1078 车载设备' : '添加 1078 车载设备'
  Object.assign(formData, {
    id: row?.id,
    simNumber: row?.simNumber || '',
    channel: row?.channel || 1,
    plateNumber: row?.plateNumber || ''
  })
  formDialogVisible.value = true
}

const handleDeleteJT1078 = (id: number) => {
  ElMessageBox.confirm('确定删除该车载视频设备吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteJT1078Device(id)
    ElMessage.success('删除成功')
    loadJT1078Devices()
  })
}

// 7. 拉流代理
const loadStreamProxies = async () => {
  loading.value = true
  try {
    const res = await getStreamProxyList({ page: 1, count: 50 })
    proxyList.value = res?.data?.list || res?.data || []
  } finally {
    loading.value = false
  }
}

const openProxyForm = (row?: any) => {
  formTitle.value = row?.id ? '编辑拉流代理' : '添加拉流代理'
  Object.assign(formData, {
    id: row?.id,
    name: row?.name || '',
    app: row?.app || 'live',
    stream: row?.stream || '',
    url: row?.url || '',
    enable: row?.enable ?? true,
    enableAudio: row?.enableAudio ?? false,
    enableMp4: false,
    mediaServerId: 'default'
  })
  formDialogVisible.value = true
}

const toggleProxyStatus = async (row: any, val: boolean) => {
  try {
    if (val) {
      await startStreamProxy(row.app, row.stream)
      ElMessage.success('已开启拉流代理')
    } else {
      await stopStreamProxy(row.app, row.stream)
      ElMessage.info('已停用拉流代理')
    }
    loadStreamProxies()
  } catch (err: any) {
    ElMessage.error(err.message || '操作失败')
  }
}

const handleDeleteProxy = (row: any) => {
  ElMessageBox.confirm(`确定删除拉流代理 ${row.app}/${row.stream} 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteStreamProxy(row.app, row.stream)
    ElMessage.success('删除成功')
    loadStreamProxies()
  })
}

// 表单提交
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  try {
    if (activeTab.value === 'onvif') {
      await saveOnvifDevice(formData)
      loadOnvifDevices()
    } else if (activeTab.value === 'hikvision') {
      await saveHikDevice(formData)
      loadHikDevices()
    } else if (activeTab.value === 'dahua') {
      await saveDahuaDevice(formData)
      loadDahuaDevices()
    } else if (activeTab.value === 'ezviz') {
      await saveEzvizDevice(formData)
      loadEzvizDevices()
    } else if (activeTab.value === 'jt1078') {
      await saveJT1078Device(formData)
      loadJT1078Devices()
    } else if (activeTab.value === 'proxy') {
      if (formData.id) {
        await updateStreamProxy(formData)
      } else {
        await addStreamProxy(formData)
      }
      loadStreamProxies()
    }
    ElMessage.success('保存成功')
    formDialogVisible.value = false
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败')
  }
}

onMounted(() => {
  loadGbDevices()
})
</script>

<style scoped lang="scss">
.media-device-management {
  padding: 10px;

  .table-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 10px;
    }
  }

  .probe-tips {
    font-size: 13px;
    color: #57606a;
    background: #f6f8fa;
    padding: 8px 12px;
    border-radius: 4px;
    border-left: 3px solid var(--el-color-primary);
  }
}
</style>
