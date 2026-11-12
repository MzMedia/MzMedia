<!-- CWMP (TR-069) 设备管理 -->
<template>
  <div>
    <!-- 连接与运行状态区域 -->
    <ContentWrap>
      <div class="flex items-center justify-between mb-4">
        <span class="text-lg font-medium">CWMP (TR-069) 连接与运行状态</span>
        <div class="space-x-2">
          <el-button type="warning" plain @click="handleConnectionRequest" :loading="wakeLoading">
            <Icon icon="ep:bell" class="mr-1" />
            主动唤醒 (Connection Request)
          </el-button>
          <el-button type="danger" plain @click="handleReboot">
            <Icon icon="ep:refresh-right" class="mr-1" />
            远程重启
          </el-button>
          <el-button type="danger" @click="handleFactoryReset">
            <Icon icon="ep:warning" class="mr-1" />
            恢复出厂
          </el-button>
          <el-button type="primary" @click="handleEditConfig">
            <Icon icon="ep:edit" class="mr-1" />
            编辑连接参数
          </el-button>
        </div>
      </div>

      <el-descriptions :column="3" border direction="horizontal">
        <el-descriptions-item label="数据模型规范">
          <el-tag size="small" :type="cwmpConfig.dataModelType === 'TR181' ? 'success' : 'info'">
            {{ cwmpConfig.dataModelType || 'TR181' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="周期上报状态">
          <el-tag size="small" :type="cwmpConfig.periodicInformEnable ? 'success' : 'danger'">
            {{ cwmpConfig.periodicInformEnable ? `开启 (${cwmpConfig.periodicInformInterval || 30}s)` : '已关闭' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最后 Inform 时间">
          {{ cwmpConfig.lastInformTime ? formatDate(cwmpConfig.lastInformTime) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="固件版本 (Software)">
          {{ cwmpConfig.softwareVersion || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="硬件版本 (Hardware)">
          {{ cwmpConfig.hardwareVersion || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="业务配置码 (ProvisioningCode)">
          {{ cwmpConfig.provisioningCode || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="ConnectionRequestURL" :span="3">
          <el-text class="w-full" truncated :title="cwmpConfig.connectionRequestUrl">
            {{ cwmpConfig.connectionRequestUrl || '等待 CPE 上报' }}
          </el-text>
        </el-descriptions-item>
      </el-descriptions>
    </ContentWrap>

    <!-- Wi-Fi 快速配置与诊断区域 -->
    <el-row :gutter="16" class="mt-4">
      <el-col :span="12">
        <ContentWrap>
          <div class="flex items-center justify-between mb-4">
            <span class="text-lg font-medium">Wi-Fi 快速设置</span>
            <div>
              <el-button size="small" @click="loadWifiConfig" :loading="wifiLoading">
                <Icon icon="ep:refresh" class="mr-1" /> 刷新
              </el-button>
              <el-button size="small" type="primary" @click="saveWifiConfig" :loading="wifiSaving">
                <Icon icon="ep:check" class="mr-1" /> 下发配置
              </el-button>
            </div>
          </div>
          <el-form :model="wifiForm" label-width="110px" v-loading="wifiLoading">
            <el-form-item label="Wi-Fi 开关">
              <el-switch v-model="wifiForm.enable" active-text="启用" inactive-text="关闭" />
            </el-form-item>
            <el-form-item label="无线 SSID">
              <el-input v-model="wifiForm.ssid" placeholder="请输入 Wi-Fi SSID" />
            </el-form-item>
            <el-form-item label="无线密码">
              <el-input v-model="wifiForm.password" placeholder="请输入 Wi-Fi 密码" show-password />
            </el-form-item>
            <el-form-item label="加密方式">
              <el-select v-model="wifiForm.securityMode" placeholder="请选择加密模式" class="!w-full">
                <el-option label="WPA2-PSK (AES)" value="WPA2-PSK" />
                <el-option label="WPA/WPA2-PSK" value="WPA-WPA2-Personal" />
                <el-option label="WPA3-SAE" value="WPA3-Personal" />
                <el-option label="开放 (无密码)" value="None" />
              </el-select>
            </el-form-item>
          </el-form>
        </ContentWrap>
      </el-col>

      <el-col :span="12">
        <ContentWrap>
          <div class="flex items-center justify-between mb-4">
            <span class="text-lg font-medium">网络 Ping 诊断测试</span>
            <el-button size="small" type="primary" @click="handleRunPing" :loading="pingLoading">
              <Icon icon="ep:video-play" class="mr-1" /> 开始诊断
            </el-button>
          </div>
          <el-form :model="pingForm" label-width="110px">
            <el-form-item label="目标主机 / IP">
              <el-input v-model="pingForm.host" placeholder="如 114.114.114.114 或 baidu.com" />
            </el-form-item>
            <el-form-item label="Ping 次数">
              <el-input-number v-model="pingForm.count" :min="1" :max="20" class="!w-full" />
            </el-form-item>
            <el-alert
              title="下发诊断指令后，CPE 将在后台执行并在下次 Inform 或诊断完成时上报结果至参数树中。"
              type="info"
              :closable="false"
              show-icon
            />
          </el-form>
        </ContentWrap>
      </el-col>
    </el-row>

    <!-- TR-069 数据模型树与参数快照区域 -->
    <ContentWrap class="mt-4">
      <div class="flex items-center justify-between mb-4">
        <span class="text-lg font-medium">TR-069 / TR-181 数据模型参数</span>
        <div class="space-x-2">
          <el-button type="success" plain @click="handleRefreshTree" :loading="paramLoading">
            <Icon icon="ep:share" class="mr-1" />
            发现参数树 (GetParameterNames)
          </el-button>
          <el-button type="primary" plain @click="handleRefreshValues" :loading="paramLoading">
            <Icon icon="ep:refresh" class="mr-1" />
            读取当前值 (GetParameterValues)
          </el-button>
        </div>
      </div>

      <!-- 搜索栏 -->
      <el-form :model="paramQueryParams" :inline="true" class="-mb-15px">
        <el-form-item label="路径前缀">
          <el-input
            v-model="paramQueryParams.pathPrefix"
            placeholder="如 InternetGatewayDevice.DeviceInfo. 或 Device."
            clearable
            class="!w-320px"
            @keyup.enter="loadParamList"
          />
        </el-form-item>
        <el-form-item>
          <el-button @click="loadParamList">
            <Icon icon="ep:search" class="mr-5px" /> 搜索
          </el-button>
          <el-button @click="resetParamQuery">
            <Icon icon="ep:refresh" class="mr-5px" /> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 参数表格 -->
      <el-table v-loading="paramLoading" :data="paramList" :stripe="true" class="mt-4" max-height="500">
        <el-table-column label="参数路径 (TR-069 / TR-181 Path)" align="left" prop="path" min-width="280">
          <template #default="{ row }">
            <el-text class="font-mono text-xs">{{ row.path }}</el-text>
          </template>
        </el-table-column>
        <el-table-column label="当前参数值" align="left" prop="value" min-width="200">
          <template #default="{ row }">
            <el-tag v-if="!row.value" size="small" type="info">空</el-tag>
            <span v-else class="font-mono">{{ row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数据类型" align="center" prop="type" width="130">
          <template #default="{ row }">
            <el-tag size="small" type="warning">{{ row.type || 'xsd:string' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="读写权限" align="center" prop="writable" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.writable ? 'success' : 'info'">
              {{ row.writable ? '可读写' : '只读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" align="center" prop="updateTime" width="170">
          <template #default="{ row }">
            {{ formatDate(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              :disabled="!row.writable"
              @click="handleEditParam(row)"
            >
              编辑值
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </ContentWrap>

    <!-- 连接配置编辑弹窗 -->
    <Dialog title="编辑 CWMP (TR-069) 连接配置" v-model="configDialogVisible" width="600px">
      <el-form ref="configFormRef" :model="configForm" label-width="150px">
        <el-form-item label="数据模型规范">
          <el-radio-group v-model="configForm.dataModelType">
            <el-radio label="TR181">TR-181 (Device.)</el-radio>
            <el-radio label="TR098">TR-098 (InternetGatewayDevice.)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="周期上报 (Inform)">
          <el-switch v-model="configForm.periodicInformEnable" />
        </el-form-item>
        <el-form-item label="上报间隔 (秒)">
          <el-input-number v-model="configForm.periodicInformInterval" :min="5" :max="86400" />
        </el-form-item>
        <el-form-item label="CPE 认证用户名">
          <el-input v-model="configForm.acsUsername" placeholder="CPE 访问 ACS 的 Basic/Digest 用户名" />
        </el-form-item>
        <el-form-item label="CPE 认证密码">
          <el-input v-model="configForm.acsPassword" placeholder="CPE 访问 ACS 的密码" show-password />
        </el-form-item>
        <el-form-item label="ACS 连接唤醒用户">
          <el-input v-model="configForm.connectionRequestUsername" placeholder="ACS 唤醒 CPE 的认证用户名" />
        </el-form-item>
        <el-form-item label="ACS 连接唤醒密码">
          <el-input v-model="configForm.connectionRequestPassword" placeholder="ACS 唤醒 CPE 的认证密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCwmpConfigSubmit" :loading="configSaving">保存</el-button>
      </template>
    </Dialog>

    <!-- 修改参数值弹窗 -->
    <Dialog title="修改 CWMP 参数值" v-model="paramDialogVisible" width="500px">
      <el-form :model="editParamForm" label-width="100px">
        <el-form-item label="参数路径">
          <el-input v-model="editParamForm.path" disabled />
        </el-form-item>
        <el-form-item label="新参数值">
          <el-input v-model="editParamForm.value" placeholder="请输入新参数值" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paramDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitParamEdit" :loading="paramSaving">下发修改</el-button>
      </template>
    </Dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils/formatTime'
import { DeviceVO } from '@/api/iot/device/device'
import { ProductVO } from '@/api/iot/product/product'
import {
  DeviceCwmpApi,
  DeviceCwmpConfigVO,
  DeviceCwmpParamVO,
  DeviceCwmpWifiVO
} from '@/api/iot/device/cwmp'

const props = defineProps<{
  device: DeviceVO
  product: ProductVO
}>()

// 1. CWMP 连接配置
const cwmpConfig = ref<DeviceCwmpConfigVO>({
  deviceId: props.device.id,
  dataModelType: 'TR181',
  periodicInformEnable: true,
  periodicInformInterval: 30
})
const configDialogVisible = ref(false)
const configSaving = ref(false)
const configForm = reactive<DeviceCwmpConfigVO>({
  deviceId: props.device.id
})

// 2. 唤醒与远程控制
const wakeLoading = ref(false)

// 3. Wi-Fi 配置
const wifiLoading = ref(false)
const wifiSaving = ref(false)
const wifiForm = reactive<DeviceCwmpWifiVO>({
  ssid: '',
  password: '',
  enable: true,
  securityMode: 'WPA2-PSK'
})

// 4. Ping 诊断
const pingLoading = ref(false)
const pingForm = reactive({
  host: '114.114.114.114',
  count: 4
})

// 5. 参数列表与编辑
const paramLoading = ref(false)
const paramList = ref<DeviceCwmpParamVO[]>([])
const paramQueryParams = reactive({
  pathPrefix: ''
})
const paramDialogVisible = ref(false)
const paramSaving = ref(false)
const editParamForm = reactive({
  path: '',
  value: ''
})

/** 加载 CWMP 连接配置 */
const loadCwmpConfig = async () => {
  try {
    const res = await DeviceCwmpApi.getCwmpConfig(props.device.id)
    if (res) {
      cwmpConfig.value = res
    }
  } catch (e) {
    console.error('加载 CWMP 配置异常', e)
  }
}

/** 打开编辑连接参数弹窗 */
const handleEditConfig = () => {
  Object.assign(configForm, cwmpConfig.value)
  configDialogVisible.value = true
}

/** 提交保存连接配置 */
const saveCwmpConfigSubmit = async () => {
  try {
    configSaving.value = true
    configForm.deviceId = props.device.id
    configForm.productId = props.device.productId
    await DeviceCwmpApi.saveCwmpConfig(configForm)
    ElMessage.success('保存 CWMP 配置成功')
    configDialogVisible.value = false
    await loadCwmpConfig()
  } finally {
    configSaving.value = false
  }
}

/** 主动唤醒 (Connection Request) */
const handleConnectionRequest = async () => {
  try {
    wakeLoading.value = true
    const res = await DeviceCwmpApi.triggerConnectionRequest(props.device.id)
    if (res) {
      ElMessage.success('已成功发送 Connection Request 唤醒信号，设备正建立会话！')
    } else {
      ElMessage.warning('发送唤醒请求失败，请检查 ConnectionRequestURL 及网络可达性')
    }
  } catch (e) {
    ElMessage.error('主动唤醒异常')
  } finally {
    wakeLoading.value = false
  }
}

/** 远程重启 */
const handleReboot = () => {
  ElMessageBox.confirm('确定要远程重启该 TR-069 设备吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await DeviceCwmpApi.reboot(props.device.id)
    ElMessage.success('重启指令已放入下发队列，将在 CPE 下次连接时生效')
  })
}

/** 恢复出厂设置 */
const handleFactoryReset = () => {
  ElMessageBox.confirm('确定要将该设备恢复出厂设置吗？该操作不可撤销！', '警告', {
    type: 'error'
  }).then(async () => {
    await DeviceCwmpApi.factoryReset(props.device.id)
    ElMessage.success('恢复出厂设置指令已放入下发队列')
  })
}

/** 加载 Wi-Fi 配置 */
const loadWifiConfig = async () => {
  try {
    wifiLoading.value = true
    const res = await DeviceCwmpApi.getWifiConfig(props.device.id)
    if (res) {
      Object.assign(wifiForm, res)
    }
  } finally {
    wifiLoading.value = false
  }
}

/** 保存 Wi-Fi 配置 */
const saveWifiConfig = async () => {
  try {
    wifiSaving.value = true
    await DeviceCwmpApi.setWifiConfig(props.device.id, wifiForm)
    ElMessage.success('Wi-Fi 修改指令已下发，CPE 会话同步后生效')
  } finally {
    wifiSaving.value = false
  }
}

/** 执行 Ping 诊断 */
const handleRunPing = async () => {
  if (!pingForm.host) {
    ElMessage.warning('请输入目标主机地址')
    return
  }
  try {
    pingLoading.value = true
    await DeviceCwmpApi.runPingDiagnostics(props.device.id, pingForm.host, pingForm.count)
    ElMessage.success('Ping 诊断任务已下发至 CPE')
  } finally {
    pingLoading.value = false
  }
}

/** 加载参数列表 */
const loadParamList = async () => {
  try {
    paramLoading.value = true
    const list = await DeviceCwmpApi.getCwmpParams(props.device.id, paramQueryParams.pathPrefix)
    paramList.value = list || []
  } finally {
    paramLoading.value = false
  }
}

const resetParamQuery = () => {
  paramQueryParams.pathPrefix = ''
  loadParamList()
}

/** 发现参数树 (GetParameterNames) */
const handleRefreshTree = async () => {
  try {
    paramLoading.value = true
    const rootPath = cwmpConfig.value.dataModelType === 'TR181' ? 'Device.' : 'InternetGatewayDevice.'
    await DeviceCwmpApi.refreshParameterNames(props.device.id, rootPath, false)
    ElMessage.success('GetParameterNames 发现指令已入队，请在 CPE 响应后点击刷新')
  } finally {
    paramLoading.value = false
  }
}

/** 刷新全部参数 (GetParameterValues) */
const handleRefreshValues = async () => {
  try {
    paramLoading.value = true
    const defaultPaths = cwmpConfig.value.dataModelType === 'TR181'
      ? ['Device.DeviceInfo.', 'Device.WiFi.']
      : ['InternetGatewayDevice.DeviceInfo.', 'InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.']
    await DeviceCwmpApi.refreshParameterValues(props.device.id, defaultPaths)
    ElMessage.success('GetParameterValues 指令已入队')
    setTimeout(loadParamList, 1500)
  } finally {
    paramLoading.value = false
  }
}

/** 编辑参数弹窗 */
const handleEditParam = (row: DeviceCwmpParamVO) => {
  editParamForm.path = row.path
  editParamForm.value = row.value
  paramDialogVisible.value = true
}

/** 提交单个参数修改 */
const submitParamEdit = async () => {
  try {
    paramSaving.value = true
    const params: Record<string, string> = {
      [editParamForm.path]: editParamForm.value
    }
    await DeviceCwmpApi.setParameterValues(props.device.id, params)
    ElMessage.success('修改指令已下发')
    paramDialogVisible.value = false
    await loadParamList()
  } finally {
    paramSaving.value = false
  }
}

onMounted(async () => {
  await loadCwmpConfig()
  await loadParamList()
  await loadWifiConfig()
})
</script>
