<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="520">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="88px"
    >
      <el-form-item label="日期" prop="date">
        <el-date-picker
          v-model="formData.date"
          type="date"
          value-format="x"
          placeholder="请选择日期"
          class="!w-1/1"
        />
      </el-form-item>
      <el-form-item label="日期类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择日期类型" class="!w-1/1">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.HRM_ATTENDANCE_HOLIDAY_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormRules } from 'element-plus'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as AttendanceHolidayApi from '@/api/hrm/attendance/holiday'
import { HrmAttendanceHolidayType } from '@/views/hrm/utils/constants'

/** 考勤节假日表单 */
defineOptions({ name: 'HrmAttendanceHolidayForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单类型：create - 新增；update - 修改
const formData = ref<AttendanceHolidayApi.HrmAttendanceHolidayVO>({
  id: undefined,
  date: undefined,
  type: HrmAttendanceHolidayType.REST
})
const formRules = reactive<FormRules>({
  date: [{ required: true, message: '日期不能为空', trigger: 'change' }],
  type: [{ required: true, message: '日期类型不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
async function open(type: string, id?: number) {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      // 获取表单数据
      formData.value = await AttendanceHolidayApi.getAttendanceHoliday(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

const emit = defineEmits(['success']) // 定义组件事件

/** 提交表单 */
async function submitForm() {
  if (!formRef.value) return
  // 校验表单
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await AttendanceHolidayApi.createAttendanceHoliday(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await AttendanceHolidayApi.updateAttendanceHoliday(formData.value)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formData.value = {
    id: undefined,
    date: undefined,
    type: HrmAttendanceHolidayType.REST
  }
  formRef.value?.resetFields()
}
</script>
