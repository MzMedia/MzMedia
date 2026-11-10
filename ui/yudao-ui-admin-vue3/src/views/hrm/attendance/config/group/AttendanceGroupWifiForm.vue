<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="560">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-form-item label="WiFi 名称" prop="ssid">
        <el-input v-model="formData.ssid" maxlength="50" placeholder="请输入 WiFi 名称" />
      </el-form-item>
      <el-form-item label="MAC 地址" prop="mac">
        <el-input v-model="formData.mac" maxlength="17" placeholder="例如 00:11:22:33:44:55" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import type { HrmAttendanceWifi } from '@/api/hrm/attendance/group'

/** 考勤组打卡 WiFi 表单 */
defineOptions({ name: 'HrmAttendanceGroupWifiForm' })

const macPattern = /^((([0-9a-f]{2}:){5})|(([0-9a-f]{2}-){5}))[0-9a-f]{2}$/i // MAC 地址格式
const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formData = ref<HrmAttendanceWifi>(createDefaultWifi()) // 表单数据
const formRules = reactive<FormRules>({
  ssid: [{ required: true, message: 'WiFi 名称不能为空', trigger: 'blur' }],
  mac: [
    { required: true, message: 'MAC 地址不能为空', trigger: 'blur' },
    { pattern: macPattern, message: 'MAC 地址格式不正确', trigger: 'blur' }
  ]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(wifi?: HrmAttendanceWifi) {
  dialogVisible.value = true
  dialogTitle.value = wifi ? '编辑打卡 WiFi' : '新增打卡 WiFi'
  resetForm()
  if (wifi) {
    formData.value = { ...wifi }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{ confirm: [wifi: HrmAttendanceWifi] }>() // 定义组件事件

/** 提交表单 */
async function submitForm() {
  // 校验表单
  await formRef.value?.validate()
  // 发送操作成功的事件
  emit('confirm', { ...formData.value })
  dialogVisible.value = false
}

/** 重置表单 */
function resetForm() {
  formData.value = createDefaultWifi()
  formRef.value?.resetFields()
}

/** 创建默认打卡 WiFi */
function createDefaultWifi(): HrmAttendanceWifi {
  return {
    ssid: '',
    mac: ''
  }
}
</script>
