<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="680px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-form-item label="证书名称" prop="name">
        <el-input v-model="formData.name" :maxlength="255" placeholder="请输入证书名称" />
      </el-form-item>
      <el-form-item label="证书级别">
        <el-input v-model="formData.level" :maxlength="255" placeholder="请输入证书级别" />
      </el-form-item>
      <el-form-item label="证书编号">
        <el-input v-model="formData.no" :maxlength="255" placeholder="请输入证书编号" />
      </el-form-item>
      <el-form-item label="有效开始日期" prop="startTime">
        <el-date-picker
          v-model="formData.startTime"
          class="!w-1/1"
          placeholder="请选择有效开始日期"
          type="date"
          value-format="x"
        />
      </el-form-item>
      <el-form-item label="有效结束日期" prop="endTime">
        <el-date-picker
          v-model="formData.endTime"
          class="!w-1/1"
          placeholder="请选择有效结束日期"
          type="date"
          value-format="x"
        />
      </el-form-item>
      <el-form-item label="发证机构">
        <el-input
          v-model="formData.issuingAuthority"
          :maxlength="255"
          placeholder="请输入发证机构"
        />
      </el-form-item>
      <el-form-item label="发证日期">
        <el-date-picker
          v-model="formData.issuingTime"
          class="!w-1/1"
          placeholder="请选择发证日期"
          type="date"
          value-format="x"
        />
      </el-form-item>
      <el-form-item label="备注">
        <el-input
          v-model="formData.remark"
          :maxlength="500"
          :rows="3"
          placeholder="请输入备注"
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="formData.sort" :min="0" class="!w-1/1" placeholder="请输入排序" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" :loading="formLoading" @click="submitForm">保存</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as CertificateApi from '@/api/hrm/employee/certificate'

defineOptions({ name: 'HrmEmployeeCertificateForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref<CertificateApi.HrmEmployeeCertificateVO>({}) // 表单数据
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '证书名称不能为空', trigger: 'blur' }],
  startTime: [{ validator: validateTimeRange, trigger: 'change' }],
  endTime: [{ validator: validateTimeRange, trigger: 'change' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref
const dialogTitle = computed(() => (formData.value.id ? '修改证书' : '新增证书')) // 弹窗的标题

/** 校验证书有效期 */
function validateTimeRange(_rule: unknown, _value: unknown, callback: (error?: Error) => void) {
  if (
    formData.value.startTime &&
    formData.value.endTime &&
    new Date(formData.value.endTime).getTime() < new Date(formData.value.startTime).getTime()
  ) {
    callback(new Error('有效结束日期不能早于有效开始日期'))
    return
  }
  callback()
}

/** 打开弹窗 */
function open(employeeId: number, row?: CertificateApi.HrmEmployeeCertificateVO) {
  dialogVisible.value = true
  resetForm()
  // 设置表单数据
  formData.value = {
    ...formData.value,
    employeeId,
    ...row
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{
  success: []
}>() // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value || !(await formRef.value.validate())) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    if (formData.value.id) {
      await CertificateApi.updateEmployeeCertificate(formData.value)
    } else {
      await CertificateApi.createEmployeeCertificate(formData.value)
    }
    message.success('保存成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formRef.value?.resetFields()
  formData.value = {
    sort: 1
  }
}
</script>
