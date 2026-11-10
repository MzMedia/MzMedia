<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="560">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
      <el-form-item label="特殊日期类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择特殊日期类型" class="!w-1/1">
          <el-option label="上班" :value="HrmAttendanceHolidayType.WORK" />
          <el-option label="休息" :value="HrmAttendanceHolidayType.REST" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期" prop="date">
        <el-date-picker
          v-model="formData.date"
          type="date"
          value-format="x"
          placeholder="请选择日期"
          class="!w-1/1"
        />
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
import type { HrmAttendanceSpecialDate } from '@/api/hrm/attendance/group'
import { HrmAttendanceHolidayType } from '@/views/hrm/utils/constants'

/** 考勤组特殊日期表单 */
defineOptions({ name: 'HrmAttendanceGroupSpecialDateForm' })

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formData = ref<HrmAttendanceSpecialDate>({}) // 表单数据
const formRules = reactive<FormRules>({
  type: [{ required: true, message: '特殊日期类型不能为空', trigger: 'change' }],
  date: [{ required: true, message: '日期不能为空', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(specialDate?: HrmAttendanceSpecialDate) {
  dialogVisible.value = true
  dialogTitle.value = specialDate ? '编辑特殊日期' : '新增特殊日期'
  resetForm()
  if (specialDate) {
    formData.value = { ...specialDate }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{ confirm: [specialDate: HrmAttendanceSpecialDate] }>() // 定义组件事件

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
  formData.value = {
    type: HrmAttendanceHolidayType.WORK,
    date: undefined
  }
  formRef.value?.resetFields()
}
</script>
