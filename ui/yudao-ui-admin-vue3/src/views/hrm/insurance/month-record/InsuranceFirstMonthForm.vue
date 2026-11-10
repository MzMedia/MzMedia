<template>
  <Dialog v-model="dialogVisible" title="新建首月社保表" width="500px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="104px"
    >
      <el-form-item label="社保月份" prop="yearMonth">
        <el-date-picker
          v-model="formData.yearMonth"
          class="!w-1/1"
          placeholder="请选择社保月份"
          type="month"
          value-format="YYYY-MM"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as InsuranceMonthRecordApi from '@/api/hrm/insurance/month-record'

defineOptions({ name: 'HrmInsuranceFirstMonthForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref({
  yearMonth: ''
})
const formRules = reactive<FormRules>({
  yearMonth: [{ required: true, message: '社保月份不能为空', trigger: 'change' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open() {
  formData.value.yearMonth = ''
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}
defineExpose({ open }) // 提供 open 方法，用于打开组件

const emit = defineEmits<{ success: [year: number] }>() // 定义组件事件

/** 提交表单 */
async function submitForm() {
  if (!formRef.value) {
    return
  }
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const [year, month] = formData.value.yearMonth.split('-').map(Number)
    await InsuranceMonthRecordApi.createFirstInsuranceMonthRecord({ year, month })
    message.success('创建成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success', year)
  } finally {
    formLoading.value = false
  }
}
</script>
