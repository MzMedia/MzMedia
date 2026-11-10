<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="680px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="112px"
    >
      <el-form-item label="联系人" prop="name">
        <el-input v-model="formData.name" :maxlength="64" placeholder="请输入联系人" />
      </el-form-item>
      <el-form-item label="关系">
        <el-input v-model="formData.relation" :maxlength="64" placeholder="请输入关系" />
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="formData.phone" :maxlength="40" placeholder="请输入电话" />
      </el-form-item>
      <el-form-item label="工作单位">
        <el-input v-model="formData.workUnit" :maxlength="128" placeholder="请输入工作单位" />
      </el-form-item>
      <el-form-item label="职务">
        <el-input v-model="formData.postName" :maxlength="128" placeholder="请输入职务" />
      </el-form-item>
      <el-form-item label="地址">
        <el-input v-model="formData.address" :maxlength="255" placeholder="请输入地址" />
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
import * as ContactApi from '@/api/hrm/employee/contact'

defineOptions({ name: 'HrmEmployeeContactForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref<ContactApi.HrmEmployeeContactVO>({}) // 表单数据
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '联系人不能为空', trigger: 'blur' }]
}) // 表单校验规则
const formRef = ref<FormInstance>() // 表单 Ref
const dialogTitle = computed(() => (formData.value.id ? '修改联系人' : '新增联系人')) // 弹窗的标题

/** 打开弹窗 */
function open(employeeId: number, row?: ContactApi.HrmEmployeeContactVO) {
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
      await ContactApi.updateEmployeeContact(formData.value)
    } else {
      await ContactApi.createEmployeeContact(formData.value)
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
