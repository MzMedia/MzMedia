<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="640">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-form-item label="地点名称" prop="name">
        <el-input v-model="formData.name" maxlength="50" placeholder="请输入地点名称" />
      </el-form-item>
      <el-form-item label="打卡地址" prop="address">
        <el-input v-model="formData.address" maxlength="255" placeholder="请选择或输入地址" />
      </el-form-item>
      <el-form-item label="经纬度" required>
        <div class="flex w-1/1 items-center gap-8px">
          <el-form-item prop="longitude" class="!mb-0 flex-1">
            <el-input-number
              v-model="formData.longitude"
              :controls="false"
              :min="-180"
              :max="180"
              :precision="6"
              class="!w-1/1"
              placeholder="经度"
            />
          </el-form-item>
          <el-form-item prop="latitude" class="!mb-0 flex-1">
            <el-input-number
              v-model="formData.latitude"
              :controls="false"
              :min="-90"
              :max="90"
              :precision="6"
              class="!w-1/1"
              placeholder="纬度"
            />
          </el-form-item>
          <el-button type="primary" @click="openMap">地图选点</el-button>
        </div>
      </el-form-item>
      <el-form-item label="打卡范围" prop="radius">
        <div class="flex items-center gap-10px">
          <el-select v-model="formData.radius" class="!w-240px">
            <el-option
              v-for="radius in HRM_ATTENDANCE_POINT_RADIUS_OPTIONS"
              :key="radius"
              :label="`${radius} 米`"
              :value="radius"
            />
          </el-select>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  <MapDialog ref="mapDialogRef" @confirm="handleMapConfirm" />
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import { MapDialog } from '@/components/Map'
import type { HrmAttendancePoint } from '@/api/hrm/attendance/group'
import { HRM_ATTENDANCE_POINT_RADIUS_OPTIONS } from '@/views/hrm/utils/constants'

/** 考勤组打卡地点表单 */
defineOptions({ name: 'HrmAttendanceGroupPointForm' })

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formData = ref<HrmAttendancePoint>(createDefaultPoint()) // 表单数据
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '地点名称不能为空', trigger: 'blur' }],
  address: [{ required: true, message: '打卡地址不能为空', trigger: 'blur' }],
  longitude: [{ required: true, message: '经度不能为空', trigger: 'change' }],
  latitude: [{ required: true, message: '纬度不能为空', trigger: 'change' }],
  radius: [{ required: true, message: '打卡范围不能为空', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref
const mapDialogRef = ref<InstanceType<typeof MapDialog>>() // 地图选点 Ref

/** 打开弹窗 */
function open(point?: HrmAttendancePoint) {
  dialogVisible.value = true
  dialogTitle.value = point ? '编辑打卡地址' : '新增打卡地址'
  resetForm()
  if (point) {
    formData.value = { ...point }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{ confirm: [point: HrmAttendancePoint] }>() // 定义组件事件

/** 提交表单 */
async function submitForm() {
  // 校验表单
  await formRef.value?.validate()
  // 发送操作成功的事件
  emit('confirm', { ...formData.value })
  dialogVisible.value = false
}

/** 打开地图坐标拾取 */
function openMap() {
  const longitude = Number.isFinite(formData.value.longitude) ? formData.value.longitude : undefined
  const latitude = Number.isFinite(formData.value.latitude) ? formData.value.latitude : undefined
  mapDialogRef.value?.open(longitude, latitude)
}

/** 回填地图坐标和地址 */
function handleMapConfirm(data: { longitude: string; latitude: string; address: string }) {
  formData.value.longitude = Number(data.longitude)
  formData.value.latitude = Number(data.latitude)
  if (data.address) {
    formData.value.address = data.address
  }
}

/** 重置表单 */
function resetForm() {
  formData.value = createDefaultPoint()
  formRef.value?.resetFields()
}

/** 创建默认打卡地点 */
function createDefaultPoint(): HrmAttendancePoint {
  return {
    name: '',
    address: '',
    latitude: undefined,
    longitude: undefined,
    radius: 300
  }
}
</script>
