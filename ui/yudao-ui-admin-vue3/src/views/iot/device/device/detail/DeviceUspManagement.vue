<!-- USP (TR-369) 设备管理 -->
<template>
  <div>
    <!-- 连接与 Agent 状态区域 -->
    <ContentWrap>
      <div class="flex items-center justify-between mb-4">
        <span class="text-lg font-medium">USP (TR-369) Agent 连接与协议状态</span>
        <div class="space-x-2">
          <el-button type="danger" plain @click="handleReboot">
            <Icon icon="ep:refresh-right" class="mr-1" />
            远程重启 (Device.Reboot)
          </el-button>
          <el-button type="danger" @click="handleFactoryReset">
            <Icon icon="ep:warning" class="mr-1" />
            恢复出厂 (Device.FactoryReset)
          </el-button>
          <el-button type="primary" @click="handleEditConfig">
            <Icon icon="ep:edit" class="mr-1" />
            编辑 USP 连接参数
          </el-button>
        </div>
      </div>

      <el-descriptions :column="3" border direction="horizontal">
        <el-descriptions-item label="Agent Endpoint ID">
          <el-tag size="small" type="primary">
            {{ uspConfig.agentEndpointId || device.serialNumber || device.deviceName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="Controller Endpoint ID">
          {{ uspConfig.controllerEndpointId || 'proto::mzmedia-controller' }}
        </el-descriptions-item>
        <el-descriptions-item label="MTP 传输层协议">
          <el-tag size="small" :type="uspConfig.mtpType === 'MQTT' ? 'success' : 'warning'">
            {{ uspConfig.mtpType || 'MQTT' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下发至 Agent Topic">
          <el-text class="font-mono text-xs">{{ uspConfig.mqttTopicAgent || `agent/${device.serialNumber || device.deviceName}` }}</el-text>
        </el-descriptions-item>
        <el-descriptions-item label="Agent 上报 Topic">
          <el-text class="font-mono text-xs">{{ uspConfig.mqttTopicController || 'controller/proto::mzmedia-controller' }}</el-text>
        </el-descriptions-item>
        <el-descriptions-item label="USP 协议版本">
          <el-tag size="small" type="info">{{ uspConfig.protocolVersion || '1.3' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最后联系时间" :span="3">
          {{ uspConfig.lastContactTime ? formatDate(uspConfig.lastContactTime) : '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </ContentWrap>

    <!-- USP Operate 操作命令调用卡片 -->
    <ContentWrap class="mt-4">
      <div class="flex items-center justify-between mb-4">
        <span class="text-lg font-medium">USP Operate 远程操作命令执行</span>
      </div>
      <el-form :model="operateForm" label-width="120px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="命令路径 (Command)">
              <el-input
                v-model="operateForm.command"
                placeholder="例如: Device.IP.Diagnostics.IPPing() 或 Device.Reboot()"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="快速模版选择">
              <el-select
                placeholder="常用 TR-181 命令模版"
                class="!w-full"
                @change="onTemplateSelect"
              >
                <el-option label="Device.Reboot() - 重启" value="Device.Reboot()" />
                <el-option label="Device.FactoryReset() - 恢复出厂" value="Device.FactoryReset()" />
                <el-option label="Device.IP.Diagnostics.IPPing() - Ping 诊断" value="Device.IP.Diagnostics.IPPing()" />
                <el-option label="Device.Download() - 固件下载" value="Device.Download()" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="入参 JSON (Args)">
          <el-input
            v-model="operateForm.argsJson"
            type="textarea"
            :rows="3"
            placeholder="JSON 键值对入参，例如: { 'Host': '114.114.114.114', 'NumberOfRepetitions': '4' }"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRunOperate" :loading="operateLoading">
            <Icon icon="ep:promotion" class="mr-1" /> 执行 Operate 指令
          </el-button>
        </el-form-item>
      </el-form>
    </ContentWrap>

    <!-- TR-181 数据模型参数快照与读写区域 -->
    <ContentWrap class="mt-4">
      <div class="flex items-center justify-between mb-4">
        <span class="text-lg font-medium">TR-181 数据模型参数列表 (USP Get / Set)</span>
        <div class="space-x-2">
          <el-button type="primary" plain @click="handleUspGet" :loading="paramLoading">
            <Icon icon="ep:refresh" class="mr-1" />
            下发 USP Get 刷新最新值
          </el-button>
        </div>
      </div>

      <!-- 搜索栏 -->
      <el-form :model="paramQueryParams" :inline="true" class="-mb-15px">
        <el-form-item label="路径过滤">
          <el-input
            v-model="paramQueryParams.pathPrefix"
            placeholder="如 Device.DeviceInfo. 或 Device.WiFi."
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
        <el-table-column label="TR-181 参数路径" align="left" prop="path" min-width="280">
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
            <el-tag size="small" type="warning">{{ row.type || 'string' }}</el-tag>
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
    <Dialog title="编辑 USP (TR-369) 连接配置" v-model="configDialogVisible" width="600px">
      <el-form ref="configFormRef" :model="configForm" label-width="170px">
        <el-form-item label="Agent Endpoint ID">
          <el-input v-model="configForm.agentEndpointId" placeholder="例如: proto::agent-001" />
        </el-form-item>
        <el-form-item label="Controller Endpoint ID">
          <el-input v-model="configForm.controllerEndpointId" placeholder="例如: proto::mzmedia-controller" />
        </el-form-item>
        <el-form-item label="MTP 协议类型">
          <el-radio-group v-model="configForm.mtpType">
            <el-radio label="MQTT">MQTT MTP</el-radio>
            <el-radio label="WEBSOCKET">WebSocket MTP</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="下发 Agent Topic">
          <el-input v-model="configForm.mqttTopicAgent" placeholder="默认: agent/{endpointId}" />
        </el-form-item>
        <el-form-item label="上报 Controller Topic">
          <el-input v-model="configForm.mqttTopicController" placeholder="默认: controller/{controllerEndpointId}" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUspConfigSubmit" :loading="configSaving">保存</el-button>
      </template>
    </Dialog>

    <!-- 修改参数值弹窗 (USP Set) -->
    <Dialog title="修改 USP 参数值 (USP Set)" v-model="paramDialogVisible" width="500px">
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
        <el-button type="primary" @click="submitParamEdit" :loading="paramSaving">下发 USP Set</el-button>
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
  DeviceUspApi,
  DeviceUspConfigVO,
  DeviceUspParamVO
} from '@/api/iot/device/usp'

const props = defineProps<{
  device: DeviceVO
  product: ProductVO
}>()

// 1. USP 连接配置
const uspConfig = ref<DeviceUspConfigVO>({
  deviceId: props.device.id,
  agentEndpointId: props.device.serialNumber || props.device.deviceName,
  controllerEndpointId: 'proto::mzmedia-controller',
  mtpType: 'MQTT',
  protocolVersion: '1.3'
})
const configDialogVisible = ref(false)
const configSaving = ref(false)
const configForm = reactive<DeviceUspConfigVO>({
  deviceId: props.device.id,
  agentEndpointId: '',
  controllerEndpointId: 'proto::mzmedia-controller',
  mtpType: 'MQTT'
})

// 2. USP Operate
const operateLoading = ref(false)
const operateForm = reactive({
  command: 'Device.Reboot()',
  argsJson: '{}'
})

// 3. 参数列表与编辑
const paramLoading = ref(false)
const paramList = ref<DeviceUspParamVO[]>([])
const paramQueryParams = reactive({
  pathPrefix: ''
})
const paramDialogVisible = ref(false)
const paramSaving = ref(false)
const editParamForm = reactive({
  path: '',
  value: ''
})

/** 加载 USP 连接配置 */
const loadUspConfig = async () => {
  try {
    const res = await DeviceUspApi.getUspConfig(props.device.id)
    if (res) {
      uspConfig.value = res
    }
  } catch (e) {
    console.error('加载 USP 配置异常', e)
  }
}

/** 打开编辑连接参数弹窗 */
const handleEditConfig = () => {
  Object.assign(configForm, uspConfig.value)
  configDialogVisible.value = true
}

/** 提交保存连接配置 */
const saveUspConfigSubmit = async () => {
  try {
    configSaving.value = true
    configForm.deviceId = props.device.id
    configForm.productId = props.device.productId
    await DeviceUspApi.saveUspConfig(configForm)
    ElMessage.success('保存 USP 配置成功')
    configDialogVisible.value = false
    await loadUspConfig()
  } finally {
    configSaving.value = false
  }
}

/** 远程重启 */
const handleReboot = () => {
  ElMessageBox.confirm('确定要向该 USP Agent 发送远程重启命令 (Device.Reboot) 吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await DeviceUspApi.reboot(props.device.id)
    ElMessage.success('已下发 Device.Reboot() 操作指令')
  })
}

/** 恢复出厂设置 */
const handleFactoryReset = () => {
  ElMessageBox.confirm('确定要向该设备发送恢复出厂设置命令吗？该操作不可撤销！', '警告', {
    type: 'error'
  }).then(async () => {
    await DeviceUspApi.factoryReset(props.device.id)
    ElMessage.success('已下发 Device.FactoryReset() 操作指令')
  })
}

/** 模版选择 */
const onTemplateSelect = (val: string) => {
  operateForm.command = val
  if (val === 'Device.IP.Diagnostics.IPPing()') {
    operateForm.argsJson = JSON.stringify({ Host: '114.114.114.114', NumberOfRepetitions: '4' }, null, 2)
  } else if (val === 'Device.Download()') {
    operateForm.argsJson = JSON.stringify({ FileType: '1 Firmware Upgrade Image', URL: 'http://example.com/firmware.bin' }, null, 2)
  } else {
    operateForm.argsJson = '{}'
  }
}

/** 执行 Operate 指令 */
const handleRunOperate = async () => {
  if (!operateForm.command) {
    ElMessage.warning('请输入命令路径')
    return
  }
  let args: Record<string, string> = {}
  if (operateForm.argsJson && operateForm.argsJson.trim() !== '') {
    try {
      args = JSON.parse(operateForm.argsJson)
    } catch (e) {
      ElMessage.error('入参 JSON 格式不正确')
      return
    }
  }

  try {
    operateLoading.value = true
    await DeviceUspApi.uspOperate({
      deviceId: props.device.id,
      command: operateForm.command,
      inputArgs: args
    })
    ElMessage.success(`Operate 命令 [${operateForm.command}] 已成功下发至 Agent`)
  } finally {
    operateLoading.value = false
  }
}

/** 加载参数列表 */
const loadParamList = async () => {
  try {
    paramLoading.value = true
    const list = await DeviceUspApi.getUspParams(props.device.id, paramQueryParams.pathPrefix)
    paramList.value = list || []
  } finally {
    paramLoading.value = false
  }
}

const resetParamQuery = () => {
  paramQueryParams.pathPrefix = ''
  loadParamList()
}

/** 下发 USP Get 获取最新值 */
const handleUspGet = async () => {
  try {
    paramLoading.value = true
    const paths = paramQueryParams.pathPrefix ? [paramQueryParams.pathPrefix] : ['Device.DeviceInfo.', 'Device.WiFi.']
    await DeviceUspApi.uspGet(props.device.id, paths)
    ElMessage.success('USP Get 请求已成功发布至 MQTT/WS MTP')
    setTimeout(loadParamList, 1500)
  } finally {
    paramLoading.value = false
  }
}

/** 编辑参数弹窗 */
const handleEditParam = (row: DeviceUspParamVO) => {
  editParamForm.path = row.path
  editParamForm.value = row.value
  paramDialogVisible.value = true
}

/** 提交单个参数修改 (USP Set) */
const submitParamEdit = async () => {
  try {
    paramSaving.value = true
    const params: Record<string, string> = {
      [editParamForm.path]: editParamForm.value
    }
    await DeviceUspApi.uspSet(props.device.id, params)
    ElMessage.success('USP Set 指令已下发')
    paramDialogVisible.value = false
    await loadParamList()
  } finally {
    paramSaving.value = false
  }
}

onMounted(async () => {
  await loadUspConfig()
  await loadParamList()
})
</script>
