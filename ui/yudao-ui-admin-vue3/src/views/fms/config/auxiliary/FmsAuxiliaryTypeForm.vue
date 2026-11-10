<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="420px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" maxlength="255" placeholder="请输入分类名称" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { FmsAuxiliaryTypeApi } from '@/api/fms/config/auxiliary/type'
import type { FmsAuxiliaryTypeVO } from '@/api/fms/config/auxiliary/type'
import { useFmsStore } from '@/views/fms/store/fms'
import type { FormInstance, FormRules } from 'element-plus'

defineOptions({ name: 'FmsAuxiliaryTypeForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const fmsStore = useFmsStore() // FMS 状态

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formData = ref<FmsAuxiliaryTypeVO>({
  id: undefined,
  accountSetId: 0,
  name: ''
})
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

/** 打开弹窗 */
function open(row?: FmsAuxiliaryTypeVO) {
  const accountSetId = fmsStore.getAccountSetId
  if (!accountSetId) return
  dialogVisible.value = true
  dialogTitle.value = row ? '编辑类别' : '新增类别'
  resetForm(accountSetId)
  // 修改时，设置数据
  if (row) {
    formData.value = { ...row }
  }
  nextTick(() => formRef.value?.clearValidate())
}
defineExpose({ open })

const emit = defineEmits<{ success: [] }>() // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    if (!formData.value.id) {
      await FmsAuxiliaryTypeApi.createAuxiliaryType(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await FmsAuxiliaryTypeApi.updateAuxiliaryType(formData.value)
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
function resetForm(accountSetId: number) {
  formData.value = {
    id: undefined,
    accountSetId,
    name: ''
  }
  formRef.value?.resetFields()
}
</script>
